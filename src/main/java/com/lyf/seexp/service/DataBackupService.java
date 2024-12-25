package com.lyf.seexp.service;

import com.lyf.seexp.config.DataBackupConfig;
import com.lyf.seexp.pojo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DataBackupService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataBackupConfig backupConfig;

    @PostConstruct
    public void initBackupSchema() {
        // 创建备份数据库schema
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + backupConfig.getBackupSchema() + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        
        // 创建备份表
        String createTableSql = String.format("""
            CREATE TABLE IF NOT EXISTS %s.event_backup (
                code VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                location VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                time TIMESTAMP,
                source_category VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                source_subcategory VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                carrier VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                disaster_category VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                disaster_subcategory VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                disaster_indicator VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
                backup_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY (code)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """, backupConfig.getBackupSchema());
        
        jdbcTemplate.execute(createTableSql);
    }

    @Scheduled(cron = "${data.backup.cron:0 0 2 * * ?}") // 每天凌晨2点执行
    @Transactional
    public void performBackup() {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(backupConfig.getTimeWindowDays(), ChronoUnit.DAYS);
        Timestamp cutoffTimestamp = Timestamp.valueOf(cutoffDate);

        // 备份旧数据
        String backupSql = String.format("""
            INSERT INTO %s.event_backup
            SELECT e.*, CURRENT_TIMESTAMP as backup_time
            FROM exp.event e
            WHERE e.time < ?
            ON DUPLICATE KEY UPDATE
                location = VALUES(location),
                time = VALUES(time),
                source_category = VALUES(source_category),
                source_subcategory = VALUES(source_subcategory),
                carrier = VALUES(carrier),
                disaster_category = VALUES(disaster_category),
                disaster_subcategory = VALUES(disaster_subcategory),
                disaster_indicator = VALUES(disaster_indicator),
                description = VALUES(description),
                backup_time = CURRENT_TIMESTAMP
            """, backupConfig.getBackupSchema());

        jdbcTemplate.update(backupSql, cutoffTimestamp);

        // 删除主表中的旧数据
        String deleteSql = "DELETE FROM exp.event WHERE time < ?";
        jdbcTemplate.update(deleteSql, cutoffTimestamp);
    }

    public void restoreData(LocalDateTime startTime, LocalDateTime endTime) {
        String restoreSql = String.format("""
            INSERT INTO exp.event
            SELECT 
                code, location, time, source_category, source_subcategory,
                carrier, disaster_category, disaster_subcategory,
                disaster_indicator, description
            FROM %s.event_backup
            WHERE time BETWEEN ? AND ?
            ON DUPLICATE KEY UPDATE
                location = VALUES(location),
                time = VALUES(time),
                source_category = VALUES(source_category),
                source_subcategory = VALUES(source_subcategory),
                carrier = VALUES(carrier),
                disaster_category = VALUES(disaster_category),
                disaster_subcategory = VALUES(disaster_subcategory),
                disaster_indicator = VALUES(disaster_indicator),
                description = VALUES(description)
            """, backupConfig.getBackupSchema());

        jdbcTemplate.update(restoreSql, Timestamp.valueOf(startTime), Timestamp.valueOf(endTime));
    }

    public List<Event> searchBackupEvents(String search) {
        String sql;
        Object[] params;
        
        if (search != null && !search.trim().isEmpty()) {
            sql = String.format("""
                SELECT code, location, time, source_category as sourceCategory,
                       source_subcategory as sourceSubcategory, carrier,
                       disaster_category as disasterCategory,
                       disaster_subcategory as disasterSubcategory,
                       disaster_indicator as disasterIndicator, description
                FROM %s.event_backup
                WHERE code LIKE ? OR location LIKE ? OR description LIKE ?
                ORDER BY time DESC
                """, backupConfig.getBackupSchema());
            String searchPattern = "%" + search.trim() + "%";
            params = new Object[]{searchPattern, searchPattern, searchPattern};
        } else {
            sql = String.format("""
                SELECT code, location, time, source_category as sourceCategory,
                       source_subcategory as sourceSubcategory, carrier,
                       disaster_category as disasterCategory,
                       disaster_subcategory as disasterSubcategory,
                       disaster_indicator as disasterIndicator, description
                FROM %s.event_backup
                ORDER BY time DESC
                """, backupConfig.getBackupSchema());
            params = new Object[]{};
        }
        
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Event.class));
    }

    @Transactional
    public void resetBackupSchema() {
        // 删除备份表
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + backupConfig.getBackupSchema() + ".event_backup");
        } catch (Exception e) {
            // 忽略错误，继续执行
        }

        // 删除schema
        try {
            jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + backupConfig.getBackupSchema());
        } catch (Exception e) {
            // 忽略错误，继续执行
        }

        // 重新初始化
        initBackupSchema();
    }

    public Integer getTimeWindow() {
        return backupConfig.getTimeWindowDays();
    }

    public void setTimeWindow(Integer days) {
        backupConfig.setTimeWindowDays(days);
    }
} 