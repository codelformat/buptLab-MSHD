package com.mshd.mshd.util;

import org.springframework.stereotype.Component;
import java.sql.*;

@Component
public class RegionCodeUtil {
    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3307/mshd";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "020111";

    public static String getRegionCode(String location) {
        String code = "000000000000"; // 默认返回值
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT code FROM region_codes WHERE region = ?")) {
            
            pstmt.setString(1, location);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    code = rs.getString("code");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error querying region code from database: " + e.getMessage());
        }
        
        return code;
    }

    public static String getLocationByCode(String code) {
        String location = "未知地区"; // 默认返回值
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT region FROM region_codes WHERE code = ?")) {
            
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    location = rs.getString("region");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error querying location from database: " + e.getMessage());
        }
        
        return location;
    }
} 