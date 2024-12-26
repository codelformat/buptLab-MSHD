package com.lyf.seexp.service.impl;

import com.lyf.seexp.mapper.CodeInfoMapper;
import com.lyf.seexp.mapper.EventMapper;
import com.lyf.seexp.mapper.RegionMapper;
import com.lyf.seexp.pojo.CodeInfo;
import com.lyf.seexp.pojo.Event;
import com.lyf.seexp.pojo.Region;
import com.lyf.seexp.service.CodeInfoService;
import com.lyf.seexp.service.EventService;
import com.lyf.seexp.utils.AliOSSUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CodeInfoServiceImpl implements CodeInfoService {
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private CodeInfoMapper codeInfoMapper;

    public CodeInfoServiceImpl() {}


    @Override
    public String getCode(CodeInfo codeInfo) {
        String locationCode = codeInfoMapper.findLocationCodeByName(codeInfo.getLocation());
        // Read the Timestamp and convert it to LocalDateTime
        Timestamp timestamp = codeInfo.getTime();
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        //localDateTime减少8小时
        // Subtract 8 hours from the LocalDateTime
        localDateTime = localDateTime.minusHours(8);
        // Corrected DateTimeFormatter pattern to use lowercase 'yyyy' for the calendar year
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        // Format the LocalDateTime to the desired string format
        String timeCode = localDateTime.format(formatter);

        //来源大类和子类
        String sourceCategory  = codeInfo.getSourceCategory();
        String sourceSubcategory = codeInfo.getSourceSubcategory();
        String sourceCode;

        switch (sourceCategory) {
            case "业务报送数据":
                // For sourceCategory = "业务报送数据", we check the subcategory
                switch (sourceSubcategory) {
                    case "前方地震应急指挥部":
                        sourceCode = "100";
                        break;
                    case "后方地震应急指挥部":
                        sourceCode = "101";
                        break;
                    case "应急指挥技术系统":
                        sourceCode = "120";
                        break;
                    case "危险区预评估工作组":
                        sourceCode = "140";
                        break;
                    case "地震应急指挥技术协调组":
                        sourceCode = "141";
                        break;
                    case "震后政府信息支持工作项目组":
                        sourceCode = "142";
                        break;
                    case "灾情快速上报接收处置系统":
                        sourceCode = "180";
                        break;
                    case "地方地震局应急信息服务相关技术系统":
                        sourceCode = "181";
                        break;
                    case "其他":
                        sourceCode = "199";
                        break;
                    default:
                        sourceCode = "199"; // Unknown subcategory
                        break;
                }
                break;

            case "泛在感知数据":
                // For sourceCategory = "泛在感知数据", we check the subcategory
                switch (sourceSubcategory) {
                    case "互联网感知":
                        sourceCode = "200";
                        break;
                    case "通信网感知":
                        sourceCode = "201";
                        break;
                    case "舆情网感知":
                        sourceCode = "202";
                        break;
                    case "电力系统感知":
                        sourceCode = "203";
                        break;
                    case "交通系统感知":
                        sourceCode = "204";
                        break;
                    case "其他":
                        sourceCode = "205";
                        break;
                    default:
                        sourceCode = "205"; // Unknown subcategory
                        break;
                }
                break;

            case "其他数据":
                // For sourceCategory = "其他数据", the subcategory is fixed to "其他"
                if ("其他".equals(sourceSubcategory)) {
                    sourceCode = "300";
                } else {
                    sourceCode = "300"; // Default to "其他"
                }
                break;

            default:
                // Unknown category
                sourceCode = "999"; // Unknown category
                break;
        }

        //载体
        String carrier = codeInfo.getCarrier();
        String carrierCode = null;
        switch (carrier) {
            case "文字":
                carrierCode = "0";
                break;
            case "图像":
                carrierCode = "1";
                break;
            case "音频":
                carrierCode = "2";
                break;
            case "视频":
                carrierCode = "3";
                break;
            case "其他":
                carrierCode = "4";
                break;
            default:
                carrierCode = "9"; // or another default/error code if needed
                break;
        }
        //灾情部分
        String disasterCategory = codeInfo.getDisasterCategory();    // 灾情大类 (Disaster Category)
        String disasterSubcategory = codeInfo.getDisasterSubcategory(); // 灾情子类 (Disaster Subcategory)
        String disasterCategoryCode;
        String disasterSubcategoryCode;

        switch (disasterCategory) {
            case "震情": // Earthquake information
                disasterCategoryCode = "1";
                switch (disasterSubcategory) {
                    case "震情信息":
                        disasterSubcategoryCode = "01";
                        break;
                    default:
                        disasterSubcategoryCode = "00"; // Unknown subcategory
                        break;
                }
                break;

            case "人员伤亡及失踪": // Casualties and missing persons
                disasterCategoryCode = "2";
                switch (disasterSubcategory) {
                    case "死亡":
                        disasterSubcategoryCode = "01";
                        break;
                    case "受伤":
                        disasterSubcategoryCode = "02";
                        break;
                    case "失踪":
                        disasterSubcategoryCode = "03";
                        break;
                    default:
                        disasterSubcategoryCode = "00"; // Unknown subcategory
                        break;
                }
                break;

            case "房屋破坏": // Building damage
                disasterCategoryCode = "3";
                switch (disasterSubcategory) {
                    case "土木":
                        disasterSubcategoryCode = "01";
                        break;
                    case "砖木":
                        disasterSubcategoryCode = "02";
                        break;
                    case "砖混":
                        disasterSubcategoryCode = "03";
                        break;
                    case "框架":
                        disasterSubcategoryCode = "04";
                        break;
                    case "其他":
                        disasterSubcategoryCode = "05";
                        break;
                    default:
                        disasterSubcategoryCode = "00"; // Unknown subcategory
                        break;
                }
                break;

            case "生命线工程灾情": // Lifeline engineering disasters
                disasterCategoryCode = "4";
                switch (disasterSubcategory) {
                    case "交通":
                        disasterSubcategoryCode = "01";
                        break;
                    case "供水":
                        disasterSubcategoryCode = "02";
                        break;
                    case "输油":
                        disasterSubcategoryCode = "03";
                        break;
                    case "燃气":
                        disasterSubcategoryCode = "04";
                        break;
                    case "电力":
                        disasterSubcategoryCode = "05";
                        break;
                    case "通信":
                        disasterSubcategoryCode = "06";
                        break;
                    case "水利":
                        disasterSubcategoryCode = "07";
                        break;
                    default:
                        disasterSubcategoryCode = "00"; // Unknown subcategory
                        break;
                }
                break;

            case "次生灾害": // Secondary disasters
                disasterCategoryCode = "5";
                switch (disasterSubcategory) {
                    case "崩塌":
                        disasterSubcategoryCode = "01";
                        break;
                    case "滑坡":
                        disasterSubcategoryCode = "02";
                        break;
                    case "泥石流":
                        disasterSubcategoryCode = "03";
                        break;
                    case "岩溶塌陷":
                        disasterSubcategoryCode = "04";
                        break;
                    case "地裂缝":
                        disasterSubcategoryCode = "05";
                        break;
                    case "地面沉降":
                        disasterSubcategoryCode = "06";
                        break;
                    case "其他（沙土液化、火灾、毒气泄漏、爆炸、污染等）":
                        disasterSubcategoryCode = "07";
                        break;
                    default:
                        disasterSubcategoryCode = "00"; // Unknown subcategory
                        break;
                }
                break;

            default:
                disasterCategoryCode = "9"; // Unknown disaster category
                disasterSubcategoryCode = "00"; // Unknown subcategory
                break;
        }

        //灾情指标
        String disasterIndicator = codeInfo.getDisasterIndicator();
        String disasterIndicatorCode;

        // Encode the disaster indicator based on the provided classification
        switch (disasterIndicator) {
            case "地理位置": // Geographical location
                disasterIndicatorCode = "001";
                break;
            case "时间": // Time
                disasterIndicatorCode = "002";
                break;
            case "震级": // Magnitude
                disasterIndicatorCode = "003";
                break;
            case "震源深度": // Hypocenter depth
                disasterIndicatorCode = "004";
                break;
            case "烈度": // Intensity
                disasterIndicatorCode = "005";
                break;
            case "受灾人数": // Affected population
                disasterIndicatorCode = "101";
                break;
            case "受灾程度": // Degree of damage
                disasterIndicatorCode = "102";
                break;
            case "一般损坏面积": // General damaged area
                disasterIndicatorCode = "201";
                break;
            case "严重损坏面积": // Severely damaged area
                disasterIndicatorCode = "202";
                break;
            case "受灾设施数": // Number of affected facilities
                disasterIndicatorCode = "301";
                break;
            case "受灾范围": // Affected area
                disasterIndicatorCode = "302";
                break;
            case "灾害损失": // Disaster loss
                disasterIndicatorCode = "401";
                break;
            case "灾害范围": // Disaster range
                disasterIndicatorCode = "402";
                break;
            default:
                disasterIndicatorCode = "999"; // Unknown indicator
                break;
        }

        //
        return locationCode+timeCode
                +sourceCode+carrierCode
                +disasterCategoryCode+disasterSubcategoryCode+disasterIndicatorCode;
    }
}
