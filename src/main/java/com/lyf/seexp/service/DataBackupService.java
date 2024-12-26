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


public interface DataBackupService {


    void initBackupSchema();

    void performBackup();

    void restoreData(LocalDateTime startTime, LocalDateTime endTime);
    List<Event> searchBackupEvents(String search);


    void resetBackupSchema();

    Integer getTimeWindow();

    void setTimeWindow(Integer days);
} 