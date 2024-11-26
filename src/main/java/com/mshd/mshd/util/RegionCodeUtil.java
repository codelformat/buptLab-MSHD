package com.mshd.mshd.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class RegionCodeUtil {
    private static Map<String, String> regionCodeMap = new HashMap<>();
    private static Map<String, String> reverseRegionCodeMap = new HashMap<>();
    private static final String JSON_CACHE_FILE = "region_code_cache.json";
    private static final String EXCEL_FILE_PATTERN = "region_code_*.xls";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3307/mshd";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "020111";

    @PostConstruct
    public void init() {
        try {
            if (!loadFromCache()) {
                loadFromExcelFiles();
                saveToCache();
            }
            // 初始化反向映射
            regionCodeMap.forEach((location, code) -> reverseRegionCodeMap.put(code, location));
            
            // 保存到MySQL
            saveToDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize RegionCodeUtil: " + e.getMessage());
        }
    }

    private boolean loadFromCache() {
        try {
            ClassPathResource resource = new ClassPathResource(JSON_CACHE_FILE);
            if (!resource.exists()) {
                return false;
            }

            try (InputStream is = resource.getInputStream()) {
                regionCodeMap = objectMapper.readValue(is, 
                    new TypeReference<Map<String, String>>() {});
                System.out.println("Successfully loaded " + regionCodeMap.size() + 
                    " region codes from cache");
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to load from cache: " + e.getMessage());
            return false;
        }
    }

    private void loadFromExcelFiles() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:" + EXCEL_FILE_PATTERN);

        if (resources.length == 0) {
            throw new FileNotFoundException("No Excel files found matching pattern: " + EXCEL_FILE_PATTERN);
        }

        int totalLoaded = 0;
        for (Resource resource : resources) {
            System.out.println("Loading from file: " + resource.getFilename());
            try (InputStream is = resource.getInputStream()) {
                int count = loadFromExcel(is);
                totalLoaded += count;
                System.out.println("Loaded " + count + " entries from " + resource.getFilename());
            } catch (IOException e) {
                System.err.println("Error loading file " + resource.getFilename() + ": " + e.getMessage());
            }
        }
        System.out.println("Total loaded entries: " + totalLoaded);
    }

    private int loadFromExcel(InputStream is) throws IOException {
        int count = 0;
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 跳过表头

            try {
                String code = getStringCellValue(row.getCell(0));
                String province = getStringCellValue(row.getCell(1));
                String city = getStringCellValue(row.getCell(2));
                String county = getStringCellValue(row.getCell(3));
                String town = getStringCellValue(row.getCell(4));
                String village = getStringCellValue(row.getCell(5));

                if (code.isEmpty() || province.isEmpty()) continue;

                String fullAddress = String.format("%s%s%s%s%s", 
                    province, city, county, town, village);
                
                regionCodeMap.put(fullAddress, code);
                count++;
            } catch (Exception e) {
                System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
            }
        }
        return count;
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

    private void saveToCache() {
        try {
            // 获取resources目录的实际文件系统路径
            String resourcePath = new ClassPathResource("").getFile().getAbsolutePath();
            File cacheFile = new File(resourcePath, JSON_CACHE_FILE);
            
            // 确保父目录存在
            if (!cacheFile.getParentFile().exists()) {
                cacheFile.getParentFile().mkdirs();
            }

            // 写入JSON缓存文件
            objectMapper.writeValue(cacheFile, regionCodeMap);
            System.out.println("Successfully saved " + regionCodeMap.size() + 
                " region codes to cache file: " + cacheFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save cache: " + e.getMessage());
        }
    }

    private void saveToDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 设置连接的字符集
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET NAMES utf8mb4");
            }

            // 检查表是否存在且字符集正确
            boolean needCreateTable = true;
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT TABLE_COLLATION " +
                    "FROM INFORMATION_SCHEMA.TABLES " +
                    "WHERE TABLE_SCHEMA = 'mshd' AND TABLE_NAME = 'region_codes'"
                );
                
                if (rs.next()) {
                    String tableCollation = rs.getString("TABLE_COLLATION");
                    if ("utf8mb4_unicode_ci".equals(tableCollation)) {
                        needCreateTable = false;
                        // 如果表存在且字符集正确，则输出提示结果
                        // stmt.execute("TRUNCATE TABLE region_codes");
                        System.out.println("The table is set correctly!!");
                    }
                }
            }

            // 如果需要，创建新表
            if (needCreateTable) {
                try (Statement stmt = conn.createStatement()) {
                    // 先删除表（如果存在）
                    stmt.execute("DROP TABLE IF EXISTS region_codes");
                    
                    // 重新创建表，确保使用正确的字符集
                    stmt.execute("CREATE TABLE region_codes (" +
                               "code VARCHAR(12) PRIMARY KEY," +
                               "region VARCHAR(255) NOT NULL" +
                               ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
                } 
                // 批量插入数据
                String sql = "INSERT INTO region_codes (code, region) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    conn.setAutoCommit(false);
                    int batchSize = 0;
                    for (Map.Entry<String, String> entry : regionCodeMap.entrySet()) {
                        pstmt.setString(1, entry.getValue()); // code
                        pstmt.setString(2, entry.getKey());   // region
                        pstmt.addBatch();
                    
                    if (++batchSize % 1000 == 0) {
                            pstmt.executeBatch();
                            conn.commit();
                        }
                    }
                    // 提交剩余的数据
                    pstmt.executeBatch();
                    conn.commit();
                    System.out.println("Successfully saved " + regionCodeMap.size() + " entries to database");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getRegionCode(String location) {
        return regionCodeMap.getOrDefault(location, "000000000000");
    }

    // 用于测试的方法
    public int getCacheSize() {
        return regionCodeMap.size();
    }

    public String getLocationByCode(String code) {
        return reverseRegionCodeMap.getOrDefault(code, "未知地区");
    }
} 