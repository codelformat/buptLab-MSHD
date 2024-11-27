package com.mshd.mshd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mshd.mshd.model.DisasterInfo;
import com.mshd.mshd.service.DisasterCodeService;
import com.mshd.mshd.service.EarthquakeInfoService;
import java.time.format.DateTimeFormatter;
import java.sql.*;

@Service
public class EarthquakeInfoServiceImpl implements EarthquakeInfoService {

    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3307/mshd";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "020111";

    @Autowired
    private DisasterCodeService disasterCodeService;

    private static final String INSERT_SQL = 
        "INSERT INTO earthquake_info (code, carrier, category, date, description, label, location, origin) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public boolean saveEncodedInfo(String code) {
        DisasterInfo info = disasterCodeService.decode(code);
        return saveInfo(info);
    }

    @Override
    public boolean saveInfo(DisasterInfo info) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
            
            String code = disasterCodeService.encode(info);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            pstmt.setString(1, code);
            pstmt.setString(2, info.getCarrier());
            pstmt.setString(3, info.getCategory());
            pstmt.setString(4, info.getTime().format(formatter));
            pstmt.setString(5, info.getDescription());
            pstmt.setString(6, info.getLabel());
            pstmt.setString(7, info.getLocation());
            pstmt.setString(8, info.getSource());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 