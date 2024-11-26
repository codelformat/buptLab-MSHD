package com.mshd.mshd.util;

import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.io.*;
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

    public void checkAndImportExcelFiles() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 匹配所有xls文件
            Resource[] resources = resolver.getResources("classpath:*.xls");

            if (resources.length == 0) {
                System.out.println("No Excel files found for import");
                return;
            }

            for (Resource resource : resources) {
                System.out.println("Attempting to process file: " + resource.getFilename());
                try {
                    importExcelFile(resource);
                } catch (Exception e) {
                    System.err.println("Failed to import file " + resource.getFilename() + 
                        ": " + e.getMessage() + ". Skipping to next file.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking for Excel files: " + e.getMessage());
        }
    }

    private void importExcelFile(Resource resource) {
        try (InputStream is = resource.getInputStream();
             Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            
            // 验证文件格式是否正确
            Row firstRow = sheet.getRow(0);
            if (!isValidExcelFormat(firstRow)) {
                System.out.println("File " + resource.getFilename() + 
                    " does not match required format. Skipping...");
                return;
            }

            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO region_codes (code, region) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE region = VALUES(region)")) {
                
                int batchSize = 0;
                int totalRows = 0;
                
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // 跳过表头

                    String code = getStringCellValue(row.getCell(0));
                    String province = getStringCellValue(row.getCell(1));
                    String city = getStringCellValue(row.getCell(2));
                    String county = getStringCellValue(row.getCell(3));
                    String town = getStringCellValue(row.getCell(4));
                    String village = getStringCellValue(row.getCell(5));

                    if (code.isEmpty() || province.isEmpty()) continue;

                    String fullAddress = String.format("%s%s%s%s%s", 
                        province, city, county, town, village);

                    pstmt.setString(1, code);
                    pstmt.setString(2, fullAddress);
                    pstmt.addBatch();
                    totalRows++;

                    if (++batchSize % 1000 == 0) {
                        pstmt.executeBatch();
                        System.out.println("Processed " + batchSize + " rows");
                    }
                }
                
                if (batchSize % 1000 != 0) {
                    pstmt.executeBatch();
                }
                
                conn.commit();
                System.out.println("Successfully imported " + totalRows + 
                    " rows from file: " + resource.getFilename());
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error importing Excel file " + resource.getFilename() + 
                ": " + e.getMessage());
        }
    }

    private boolean isValidExcelFormat(Row headerRow) {
        if (headerRow == null) return false;
        
        // 检查是否至少有6列
        if (headerRow.getLastCellNum() < 6) return false;
        
        // 这里可以根据需要添加更多的格式验证
        // 比如检查表头名称是否符合预期等
        return true;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }
} 