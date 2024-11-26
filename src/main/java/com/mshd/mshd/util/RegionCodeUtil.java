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
import java.util.HashMap;
import java.util.Map;

@Component
public class RegionCodeUtil {
    private static Map<String, String> regionCodeMap = new HashMap<>();
    private static final String JSON_CACHE_FILE = "region_code_cache.json";
    private static final String EXCEL_FILE_PATTERN = "region_code_*.xls";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            if (!loadFromCache()) {
                loadFromExcelFiles();
                saveToCache();
            }
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

    public static String getRegionCode(String location) {
        return regionCodeMap.getOrDefault(location, "000000000000");
    }

    // 用于测试的方法
    public int getCacheSize() {
        return regionCodeMap.size();
    }
} 