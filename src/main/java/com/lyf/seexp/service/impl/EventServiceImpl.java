package com.lyf.seexp.service.impl;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lyf.seexp.mapper.EventMapper;
import com.lyf.seexp.mapper.RegionMapper;
import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.pojo.Region;
import com.lyf.seexp.service.EventService;
import com.lyf.seexp.utils.AliOSSUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;


import javax.swing.*;
import java.io.*;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private RegionMapper regionMapper;

    public EventServiceImpl() {}

    //格式yyyymmddhhmmss转化为Timestamp
    private Timestamp to_timestamp(String timeString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            java.util.Date parsedDate = dateFormat.parse(timeString);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            System.err.println("Error parsing time string: " + timeString);
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public final Event decode(String code) {
        //地区
        String regionString = code.substring(0,12);
        Region region=null;
        try {
            region = regionMapper.findByCode(regionString);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("region 读取失败!");
        }
        String location = region.getName();
        //时间
        String timeString = code.substring(12,26);
        Timestamp time = to_timestamp(timeString);
        //来源大类 子类
        String sourceString = code.substring(26,29);
        String sourceCategory,sourceSubcategory;
        switch (sourceString.substring(0, 1)) {
            case "1":
                sourceCategory = "业务报送数据";
                switch (sourceString.substring(1, 3)) {
                    case "00":
                        sourceSubcategory = "前方地震应急指挥部";
                        break;
                    case "01":
                        sourceSubcategory = "后方地震应急指挥部";
                        break;
                    case "20":
                        sourceSubcategory = "应急指挥技术系统";
                        break;
                    case "40":
                        sourceSubcategory = "危险区预评估工作组";
                        break;
                    case "41":
                        sourceSubcategory = "地震应急指挥技术协调组";
                        break;
                    case "42":
                        sourceSubcategory = "震后政府信息支持工作项目组";
                        break;
                    case "80":
                        sourceSubcategory = "灾情快速上报接收处置系统";
                        break;
                    case "81":
                        sourceSubcategory = "地方地震局应急信息服务相关技术系统";
                        break;
                    case "99":
                        sourceSubcategory = "其他";
                        break;
                    default:
                        sourceSubcategory = "未知子类";
                        break;
                }
                break;
            case "2":
                sourceCategory = "泛在感知数据";
                switch (sourceString.substring(1, 3)) {
                    case "00":
                        sourceSubcategory = "互联网感知";
                        break;
                    case "01":
                        sourceSubcategory = "通信网感知";
                        break;
                    case "02":
                        sourceSubcategory = "舆情网感知";
                        break;
                    case "03":
                        sourceSubcategory = "电力系统感知";
                        break;
                    case "04":
                        sourceSubcategory = "交通系统感知";
                        break;
                    case "05":
                        sourceSubcategory = "其他";
                        break;
                    default:
                        sourceSubcategory = "未知子类";
                        break;
                }
                break;
            case "3":
                sourceCategory = "其他数据";
                sourceSubcategory = "其他";
                break;
            default:
                sourceCategory = "未知大类";
                sourceSubcategory = "未知子类";
                break;
        }
        //载体
        String carrierString = code.substring(29,30);
        String carrier;
        switch (carrierString) {
            case "0":
                carrier = "文字";
                break;
            case "1":
                carrier = "图像";
                break;
            case "2":
                carrier = "音频";
                break;
            case "3":
                carrier = "视频";
                break;
            case "4":
                carrier = "其他";
                break;
            default:
                carrier = "未知载体";
                break;
        }

        //灾情信息
        String disasterCategoryString = code.substring(30,31);
        String disasterSubcategoryString = code.substring(31,33);
        String disasterCategory = null;
        String disasterSubcategory = null;

        // Decode the disaster category
        switch (disasterCategoryString) {
            case "1":
                disasterCategory = "震情"; // Earthquake information
                switch (disasterSubcategoryString) {
                    case "01":
                        disasterSubcategory = "震情信息";
                        break;
                    default:
                        disasterSubcategory = "未知子类";
                        break;
                }
                break;
            case "2":
                disasterCategory = "人员伤亡及失踪"; // Casualties and missing persons
                switch (disasterSubcategoryString) {
                    case "01":
                        disasterSubcategory = "死亡";
                        break;
                    case "02":
                        disasterSubcategory = "受伤";
                        break;
                    case "03":
                        disasterSubcategory = "失踪";
                        break;
                    default:
                        disasterSubcategory = "未知子类";
                        break;
                }
                break;
            case "3":
                disasterCategory = "房屋破坏"; // Building damage
                switch (disasterSubcategoryString) {
                    case "01":
                        disasterSubcategory = "土木";
                        break;
                    case "02":
                        disasterSubcategory = "砖木";
                        break;
                    case "03":
                        disasterSubcategory = "砖混";
                        break;
                    case "04":
                        disasterSubcategory = "框架";
                        break;
                    case "05":
                        disasterSubcategory = "其他";
                        break;
                    default:
                        disasterSubcategory = "未知子类";
                        break;
                }
                break;
            case "4":
                disasterCategory = "生命线工程灾情"; // Lifeline engineering disasters
                switch (disasterSubcategoryString) {
                    case "01":
                        disasterSubcategory = "交通";
                        break;
                    case "02":
                        disasterSubcategory = "供水";
                        break;
                    case "03":
                        disasterSubcategory = "输油";
                        break;
                    case "04":
                        disasterSubcategory = "燃气";
                        break;
                    case "05":
                        disasterSubcategory = "电力";
                        break;
                    case "06":
                        disasterSubcategory = "通信";
                        break;
                    case "07":
                        disasterSubcategory = "水利";
                        break;
                    default:
                        disasterSubcategory = "未知子类";
                        break;
                }
                break;
            case "5":
                disasterCategory = "次生灾害"; // Secondary disasters
                switch (disasterSubcategoryString) {
                    case "01":
                        disasterSubcategory = "崩塌";
                        break;
                    case "02":
                        disasterSubcategory = "滑坡";
                        break;
                    case "03":
                        disasterSubcategory = "泥石流";
                        break;
                    case "04":
                        disasterSubcategory = "岩溶塌陷";
                        break;
                    case "05":
                        disasterSubcategory = "地裂缝";
                        break;
                    case "06":
                        disasterSubcategory = "地面沉降";
                        break;
                    case "07":
                        disasterSubcategory = "其他（沙土液化、火灾、毒气泄漏、爆炸、污染等）";
                        break;
                    default:
                        disasterSubcategory = "未知子类";
                        break;
                }
                break;
            default:
                disasterCategory = "未知大类";
                disasterSubcategory = "未知子类";
                break;
        }

        //灾情指标
        String disasterIndicatorString = code.substring(33, 36);
        String disasterIndicator = null;

        // Decode the disaster indicator based on the provided classification
        switch (disasterIndicatorString) {
            case "001":
                disasterIndicator = "地理位置"; // Geographical location
                break;
            case "002":
                disasterIndicator = "时间"; // Time
                break;
            case "003":
                disasterIndicator = "震级"; // Magnitude
                break;
            case "004":
                disasterIndicator = "震源深度"; // Hypocenter depth
                break;
            case "005":
                disasterIndicator = "烈度"; // Intensity
                break;
            case "101":
                disasterIndicator = "受灾人数"; // Affected population
                break;
            case "102":
                disasterIndicator = "受灾程度"; // Degree of damage
                break;
            case "201":
                disasterIndicator = "一般损坏面积"; // General damaged area
                break;
            case "202":
                disasterIndicator = "严重损坏面积"; // Severely damaged area
                break;
            case "203":
                disasterIndicator = "受灾程度"; // Degree of damage
                break;
            case "301":
                disasterIndicator = "受灾设施数"; // Number of affected facilities
                break;
            case "302":
                disasterIndicator = "受灾范围"; // Affected area
                break;
            case "303":
                disasterIndicator = "受灾程度"; // Degree of damage
                break;
            case "401":
                disasterIndicator = "灾害损失"; // Disaster loss
                break;
            case "402":
                disasterIndicator = "灾害范围"; // Disaster range
                break;
            case "403":
                disasterIndicator = "受灾程度"; // Degree of damage
                break;
            default:
                disasterIndicator = "未知指标"; // Unknown indicator
                break;
        }
        //return null;
        return new Event(code, location, time,
                    sourceCategory, sourceSubcategory,
                    carrier,
                    disasterCategory, disasterSubcategory, disasterIndicator, "");
    }

    @Override
    public void addItem(Event event) {
        eventMapper.add(event);
    }

    @Override
    public void addItemFromCode(String encodedEvent) {
        Event event = decode(encodedEvent);
        eventMapper.add(event);
    }

    @Override
    public void addTextItem(String eventCode, String text) {

    }

    //.xls或.xlsx文件
    private File selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("请选择一个文件");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Filter for .xls and .xlsx files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".xls") || f.getName().toLowerCase().endsWith(".xlsx");
            }

            @Override
            public String getDescription() {
                return "Excel Files (*.xls, *.xlsx)";
            }
        });

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
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
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    private ArrayList<Event> readExcel(File file){
        ArrayList<Event> events = new ArrayList<>();
        String fileName = file.getName().toLowerCase();
        try (InputStream inputStream = new FileInputStream(file)) {
            Workbook workbook;

            if (fileName.endsWith(".xls")) {
                // Use POIFSFileSystem for .xls files
                POIFSFileSystem fs = new POIFSFileSystem(inputStream);
                workbook = new HSSFWorkbook(fs);
            } else if (fileName.endsWith(".xlsx")) {
               workbook = new XSSFWorkbook(new FileInputStream(file));
            } else {
                throw new IllegalArgumentException("Unsupported file format: " + fileName);
            }

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            //可加上对表的限制的代码判断(只能有两列……)

            // Iterate through rows and cells
            for (Row row : sheet) {
                int i = 0;
                Event event = new Event();
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    if (i == 0) {
                        String code = cellValue;
                        if (code.equals("id")) break;
                        if (code.isEmpty()) break;
                        event = decode(code);
                    }
                    if (i == 1) {
                        String desription = cellValue;
                        event.setDescription(desription);
                        events.add(event);
                        break;
                    }
                    ++i;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public ArrayList<Event> readXlsFile(){
        ArrayList<Event> events = new ArrayList<>();
        try {
            File file = new File("D:\\3g1s\\seexp\\SEExp\\test.xlsx");
            events = readExcel(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    //上传文件
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String url = null;
        try {
            url = AliOSSUtils.upload(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    @Override
    public ArrayList<Event> readXlsxFile(InputStream inputStream) {
        ArrayList<Event> events = new ArrayList<>();
        final int BATCH_SIZE = 1000;
        int batchCount = 0;
        int totalRows = 0;

        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // 验证表头（可选）
            Row headerRow = sheet.getRow(0);
            if (headerRow != null && "id".equals(getCellValue(headerRow.getCell(0)))) {
                // 跳过表头
                totalRows++;
            }

            for (Row row : sheet) {
                if (row.getRowNum() < totalRows) continue; // 跳过已处理的行（包括表头）

                String code = getCellValue(row.getCell(0));
                String description = getCellValue(row.getCell(1));

                if (code.isEmpty()) continue;

                try {
                    Event event = decode(code);
                    event.setDescription(description);
                    events.add(event);
                    
                    batchCount++;
                    totalRows++;

                    // 每处理BATCH_SIZE条数据，就批量插入数据库
                    if (batchCount >= BATCH_SIZE) {
                        batchInsertEvents(events);
                        events.clear();
                        batchCount = 0;
                        System.out.println("Processed " + totalRows + " rows");
                    }
                } catch (Exception e) {
                    System.err.println("Error processing row " + (totalRows + 1) + ": " + e.getMessage());
                    continue;
                }
            }

            // 处理剩余的数据
            if (!events.isEmpty()) {
                batchInsertEvents(events);
                System.out.println("Processed " + totalRows + " rows in total");
            }

            workbook.close();
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }

        return events;
    }

    private void batchInsertEvents(List<Event> events) {
        if (events.isEmpty()) return;
        
        try {
            for (Event event : events) {
                eventMapper.add(event);
            }
        } catch (Exception e) {
            System.err.println("Error batch inserting events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> getEventList() {
        try {
            return eventMapper.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch event list");
        }
    }

    @Override
    public void deleteByCode(String code) {
        eventMapper.deleteByCode(code);
    }

    @Override
    public void updateByCode(Event event) {
        eventMapper.updateByCode(event);
    }

    @Override
    public List<Event> searchEvents(String query) {
        try {
            return eventMapper.searchEvents(query);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to search events");
        }
    }
}
