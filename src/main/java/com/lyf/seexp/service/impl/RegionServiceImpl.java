package com.lyf.seexp.service.impl;

import com.lyf.seexp.mapper.RegionMapper;
import com.lyf.seexp.pojo.Region;
import com.lyf.seexp.service.RegionService;
import com.lyf.seexp.utils.AliOSSUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Override
    public Region getRegionByCode(String code) {
        return regionMapper.findByCode(code);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long)cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    @Override
    public List<Region> importRegionsFromExcel(InputStream inputStream) throws Exception {
        List<Region> regions = new ArrayList<>();
        final int BATCH_SIZE = 1000;
        int batchCount = 0;
        int totalRows = 0;

        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // 验证表头（可选）
            Row headerRow = sheet.getRow(0);
            if (headerRow != null && "code".equals(getCellValue(headerRow.getCell(0)))) {
                // 跳过表头
                totalRows++;
            }

            for (Row row : sheet) {
                if (row.getRowNum() < totalRows) continue; // 跳过已处理的行（包括表头）

                String code = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(1));

                if (code.isEmpty()) continue;

                try {
                    Region region = new Region();
                    region.setCode(code);
                    region.setName(name);
                    regions.add(region);
                    
                    batchCount++;
                    totalRows++;

                    // 每处理BATCH_SIZE条数据，就批量插入数据库
                    if (batchCount >= BATCH_SIZE) {
                        batchInsertRegions(regions);
                        regions.clear();
                        batchCount = 0;
                        System.out.println("Processed " + totalRows + " rows");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing row " + (totalRows + 1) + ": " + e.getMessage());
                    continue;
                }
            }

            // 处理剩余的数据
            if (!regions.isEmpty()) {
                batchInsertRegions(regions);
                System.out.println("Processed " + totalRows + " rows in total");
            }

            workbook.close();
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            throw e;
        }

        return regions;
    }

    private void batchInsertRegions(List<Region> regions) {
        if (regions.isEmpty()) return;
        
        try {
            for (Region region : regions) {
                Region existingRegion = regionMapper.findByCode(region.getCode());
                if (existingRegion != null) {
                    // 如果记录已存在，则更新
                    regionMapper.update(region);
                } else {
                    // 如果记录不存在，则插入
                    regionMapper.insert(region);
                }
            }
        } catch (Exception e) {
            System.err.println("Error batch inserting regions: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public String uploadAndImportRegions(MultipartFile file) throws Exception {
        String fileUrl = aliOSSUtils.upload(file);
        
        try {
            // 创建URL对象
            URL url = new URL(fileUrl);
            // 打开URL连接
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();
            
            // 导入数据
            List<Region> regions = importRegionsFromExcel(inputStream);
            inputStream.close();
            
            return "Successfully imported " + regions.size() + " regions";
        } catch (Exception e) {
            throw new Exception("Failed to import regions: " + e.getMessage());
        }
    }
}
