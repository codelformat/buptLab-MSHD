package com.lyf.seexp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBackupConfig {
    
    @Value("${data.backup.time-window:365}")  // 默认365天的时间窗口
    private Integer timeWindowDays;
    
    @Value("${data.backup.cron:0 0 2 * * ?}")  // 默认每天凌晨2点执行
    private String backupCron;
    
    @Value("${data.backup.schema:exp_backup}")  // 备份数据库schema名称
    private String backupSchema;

    public Integer getTimeWindowDays() {
        return timeWindowDays;
    }

    public void setTimeWindowDays(Integer timeWindowDays) {
        if (timeWindowDays != null && timeWindowDays > 0) {
            this.timeWindowDays = timeWindowDays;
        }
    }

    public String getBackupCron() {
        return backupCron;
    }

    public String getBackupSchema() {
        return backupSchema;
    }
} 