package com.mshd.mshd.service.impl;

import com.mshd.mshd.model.DisasterInfo;
import com.mshd.mshd.service.DisasterCodeService;
import com.mshd.mshd.util.RegionCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DisasterCodeServiceImpl implements DisasterCodeService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final RegionCodeUtil regionCodeUtil;

    @Autowired
    public DisasterCodeServiceImpl(RegionCodeUtil regionCodeUtil) {
        this.regionCodeUtil = regionCodeUtil;
    }

    @Override
    public String encode(DisasterInfo info) {
        StringBuilder code = new StringBuilder();
        
        // 添加地理信息码（12位）
        code.append(encodeLocation(info.getLocation()));
        
        // 添加时间码（14位）
        code.append(info.getTime().format(DATE_FORMATTER));
        
        // 添加来源码（3位）
        code.append(encodeSource(info.getSource()));
        
        // 添加载体码（1位）
        code.append(encodeCarrier(info.getCarrier()));
        
        // 添加灾情码（6位）
        code.append(encodeDisasterInfo(info.getCategory(), info.getLabel()));
        
        return code.toString();
    }

    @Override
    public DisasterInfo decode(String code) {
        if (code == null || code.length() != 36) {
            throw new IllegalArgumentException("Invalid disaster code format");
        }

        DisasterInfo info = new DisasterInfo();
        info.setId(code);
        
        // 解析地理信息（前12位）
        info.setLocation(decodeLocation(code.substring(0, 12)));
        
        // 解析时间信息（接下来14位）
        info.setTime(LocalDateTime.parse(code.substring(12, 26), DATE_FORMATTER));
        
        // 解析来源信息（接下来3位）
        info.setSource(decodeSource(code.substring(26, 29)));
        
        // 解析载体信息（接下来1位）
        info.setCarrier(decodeCarrier(code.substring(29, 30)));
        
        // 解析灾情信息（最后6位）
        decodeDisasterInfo(code.substring(30), info);
        
        return info;
    }

    // 编码辅助方法
    private String encodeLocation(String location) {
        return RegionCodeUtil.getRegionCode(location);
    }

    private String encodeSource(String source) {
        // 实现来源编码逻辑
//         | 序号   | 大类         | 大类代码   | 子类                                | 子类代码   |
// |--------|--------------|------------|-------------------------------------|------------|
// |        |              |            | 后方地震应急指挥部                  | 01         |
// |        |              |            | 应急指挥技术系统                    | 20         |
// |        |              |            | 社会服务工程应急救援系统            | 21         |
// |        |              |            | 危险区预评估工作组                  | 40         |
// |        |              |            | 地震应急指挥技术协调组              | 41         |
// |        |              |            | 震后政府信息支持工作项目 स्क         | 42         |
// |        |              |            | 灾情快速上报接收处理系统            | 80         |
// |        |              |            | 地方地震局应急信息服务相 关技术系统 | 81         |
// |        |              |            | 其他                                | 99         |
// | 2      | 泛在感知数据 | 2          | 互联网感知                          | 00         |
// |        |              |            | 通信网感知                          | 01         |
// |        |              |            | 舆情网感知                          | 02         |
// |        |              |            | 电力系统感知                        | 03         |
// |        |              |            | 交通系统感知                        | 04         |
// |        |              |            | 其他                                | 05         |
// | 3      | 其他数据     | 3          |                                     | 00         |
        switch (source) {
            // 大类代码 1, 业务报送数据
            case "业务报送数据": return "100";
            case "后方地震应急指挥部": return "101";
            case "应急指挥技术系统": return "120";
            case "社会服务工程应急救援系统": return "121";
            case "危险区预评估工作组": return "140";
            case "地震应急指挥技术协调组": return "141";
            case "震后政府信息支持工作项目": return "142";
            case "灾情快速上报接收处理系统": return "180";
            case "地方地震局应急信息服务相关技术系统": return "181";
            case "其他业务报送数据": return "199";
            // 大类代码 2, 泛在感知数据
            case "互联网感知": return "200";
            case "通信网感知": return "201";
            case "舆情网感知": return "202";
            case "电力系统感知": return "203";
            case "交通系统感知": return "204";
            case "其他泛在感知数据": return "205";
            // 大类代码 3, 其他数据
            case "其他数据": return "300";
            default: return "000";
        }
    }

    private String encodeCarrier(String carrier) {
//         |      | 表4 载体形式编码表   |      |
// |------|----------------------|------|
// | 序号 | 载体形式             | 编码 |
// | 1    | 文字                 | 0    |
// | 2    | 图像                 | ـــ  |
// | 3    | 音频                 | 2    |
// | 4    | 视频                 | 3    |
// | 5    | 其他                 | 4    |
        // 实现载体编码逻辑
        switch (carrier) {
            case "文字": return "0";
            case "图像": return "1";
            case "音频": return "2";
            case "视频": return "3";
            case "其他": return "4";
            default: return "0";
        } 
    }

    private String encodeDisasterInfo(String category, String label) {
        // 实现灾情信息编码逻辑

//         |      |                | 表 6 灾情信息分类表   |                                           |          |
// |------|----------------|-----------------------|-------------------------------------------|----------|
// | 序号 | 大类           | 大类代码              | 子类                                      | 子类代码 |
// | 1    | 震情           | 1                     | 震情信息                                  | 01       |
// | 2    | 人员伤亡及失踪 | 2                     | 死亡                                      | 01       |
// |      |                |                       | 受伤                                      | 02       |
// |      |                |                       | 失踪                                      | 03       |
// | 3    | 房屋破坏       | 3                     | 土木                                      | 01       |
// |      |                |                       | 砖木                                      | 02       |
// |      |                |                       | 砖混                                      | 03       |
// |      |                |                       | 框架                                      | 04       |
// |      | 生命线工程灾情 | 4                     | 其他                                      | 05       |
// |      |                |                       | 交通                                      | 01       |
// |      |                |                       | 供水                                      | 02       |
// |      |                |                       | 輸油                                      | 03       |
// | 4    |                |                       | 燃气                                      | 04       |
// |      |                |                       | 电力                                      | 05       |
// |      |                |                       | 通信                                      | 06       |
// |      |                |                       | 水利                                      | 07       |
// | 5    | 次生灾害       | 5                     | 崩塌                                      | 01       |
// |      |                |                       | 滑坡                                      | 02       |
// |      |                |                       | 泥石流                                    | 03       |
// |      |                |                       | 岩溶塌陷                                  | 04       |
// |      |                |                       | 地裂缝                                    | 05       |
// |      |                |                       | 地面沉降                                  | 06       |
// |      |                |                       | 其他(沙土液 化、火灾、毒气 泄露、爆炸、环 | 07       |
// |      |                |                       | 境污染、瘟疫、                            |          |
// |      |                |                       | 海啸等)                                   |          |

// | 表 8 灾情指标代码表   |              |          |      |
// |-----------------------|--------------|----------|------|
// | 序号                  | 灾情指标分类 | 灾情指标 | 代码 |

// 表 6 灾情信息分类表

// | 1   |                    | 地理位置     | 001   |
// |-----|--------------------|--------------|-------|
// | 2   |                    | 时间         | 002   |
// | 3   | 地震事件信息       | 震级         | 003   |
// | ব   |                    | 震源深度     | 004   |
// | 5   |                    | 烈度         | 005   |
// | ნ   | 人员伤亡及失踪信息 | 受灾人数     | 001   |
// | 7   |                    | 受灾程度     | 002   |
// | 8   |                    | 般损坏面积   | 001   |
// | g   | 房屋破坏信息       | 严重损坏面积 | 002   |
// | 10  |                    | 受灾程度     | 003   |
// | 11  |                    | 受灾设施数   | 001   |
// | 12  | 生命线工程灾情信息 | 受灾范围     | 002   |
// | 13  |                    | 受灾程度     | 003   |
// | 14  |                    | 灾害损失     | 001   |
// | 12  | 次生灾害信息       | 灾害范围     | 002   |
// | 16  |                    | 受灾程度     | 003   |

        String categoryCode = "";
        // 编码category灾情信息分类
        switch(category){
            // 大类代码 1, 震情
            case "震情": categoryCode = "101"; break;
            // 大类代码 2, 人员伤亡及失踪
            case "死亡": categoryCode = "201"; break;
            case "受伤": categoryCode = "202"; break;
            case "失踪": categoryCode = "203"; break;
            // 大类代码 3, 房屋破坏
            case "土木": categoryCode = "301"; break;
            case "砖木": categoryCode = "302"; break;
            case "砖混": categoryCode = "303"; break;
            case "框架": categoryCode = "304"; break;
            case "其他": categoryCode = "305"; break;
            // 大类代码 4, 生命线工程灾情
            case "交通": categoryCode = "401"; break;
            case "供水": categoryCode = "402"; break;
            case "输油": categoryCode = "403"; break;
            case "燃气": categoryCode = "404"; break;
            case "电力": categoryCode = "405"; break;
            case "通信": categoryCode = "406"; break;
            case "水利": categoryCode = "407"; break;
            // 大类代码 5, 次生灾害
            case "崩塌": categoryCode = "501"; break;
            case "滑坡": categoryCode = "502"; break;
            case "泥石流": categoryCode = "503"; break;
            case "岩溶塌陷": categoryCode = "504"; break;
            case "地裂缝": categoryCode = "505"; break;
            case "地面沉降": categoryCode = "506"; break;
            case "其他（沙土液化、火灾、毒气泄露、爆炸、环境污染、瘟疫、海啸等）": categoryCode = "507"; break;
            default: categoryCode = "000";
        }

        // 编码label灾情指标
        String labelCode = "";
        switch(categoryCode.charAt(0)){
            case '1': // 大类代码 1, 地震事件信息（震情）
                switch(label){
                    case "地理位置": labelCode = "003"; break;
                    case "时间": labelCode = "002"; break;
                    case "震级": labelCode = "003"; break;
                    case "震源深度": labelCode = "004"; break;
                    case "烈度": labelCode = "005"; break;
                    default: labelCode = "000";
                }
                break;
            case '2': // 大类代码 2, 人员伤亡及失踪
                switch(label){
                    case "受灾人数": labelCode = "001"; break;
                    case "受灾程度": labelCode = "002"; break;
                    default: labelCode = "000";
                }
                break;
            case '3': // 大类代码 3, 房屋破坏
                switch(label){
                    case "一般损坏面积": labelCode = "001"; break;
                    case "严重损坏面积": labelCode = "002"; break;
                    case "受灾程度": labelCode = "003"; break;
                    default: labelCode = "000";
                }
                break;
            case '4': // 大类代码 4, 生命线工程灾情
                switch(label){
                    case "受灾设施数": labelCode = "001"; break;
                    case "受灾范围": labelCode = "002"; break;
                    case "受灾程度": labelCode = "003"; break;
                    default: labelCode = "000";
                }
                break;
            case '5': // 大类代码 5, 次生灾害
                switch (label) {
                    case "灾害损失": labelCode = "001"; break;
                    case "灾害范围": labelCode = "002"; break;
                    case "受灾程度": labelCode = "003"; break;
                    default: labelCode = "000";
                }
            default: labelCode = "000";
        }

        // 返回编码
        return categoryCode + labelCode;
    }

    // 解码辅助方法
    private String decodeLocation(String code) {
        return regionCodeUtil.getLocationByCode(code);
    }

    private String decodeSource(String code) {
        switch (code) {
            // 大类代码 1, 业务报送数据
            case "100": return "业务报送数据";
            case "101": return "后方地震应急指挥部";
            case "120": return "应急指挥技术系统";
            case "121": return "社会服务工程应急救援系统";
            case "140": return "危险区预评估工作组";
            case "141": return "地震应急指挥技术协调组";
            case "142": return "震后政府信息支持工作项目";
            case "180": return "灾情快速上报接收处理系统";
            case "181": return "地方地震局应急信息服务相关技术系统";
            case "199": return "其他业务报送数据";
            // 大类代码 2, 泛在感知数据
            case "200": return "互联网感知";
            case "201": return "通信网感知";
            case "202": return "舆情网感知";
            case "203": return "电力系统感知";
            case "204": return "交通系统感知";
            case "205": return "其他泛在感知数据";
            // 大类代码 3, 其他数据
            case "300": return "其他数据";
            default: return "未知来源";
        }
    }

    private String decodeCarrier(String code) {
        switch (code) {
            case "0": return "文字";
            case "1": return "图像";
            case "2": return "音频";
            case "3": return "视频";
            case "4": return "其他";
            default: return "未知载体";
        }
    }

    private void decodeDisasterInfo(String code, DisasterInfo info) {
        if (code == null || code.length() != 6) {
            throw new IllegalArgumentException("Invalid disaster info code");
        }

        String categoryCode = code.substring(0, 3);
        String labelCode = code.substring(3);

        // 解码category（前3位）
        String category = decodeCategoryFromCode(categoryCode);
        info.setCategory(category);

        // 解码label（后3位）
        String label = decodeLabelFromCode(categoryCode.charAt(0), labelCode);
        info.setLabel(label);
    }

    private String decodeCategoryFromCode(String categoryCode) {
        switch (categoryCode) {
            // 震情
            case "101": return "震情";
            // 人员伤亡及失踪
            case "201": return "死亡";
            case "202": return "受伤";
            case "203": return "失踪";
            // 房屋破坏
            case "301": return "土木";
            case "302": return "砖木";
            case "303": return "砖混";
            case "304": return "框架";
            case "305": return "其他";
            // 生命线工程灾情
            case "401": return "交通";
            case "402": return "供水";
            case "403": return "输油";
            case "404": return "燃气";
            case "405": return "电力";
            case "406": return "通信";
            case "407": return "水利";
            // 次生灾害
            case "501": return "崩塌";
            case "502": return "滑坡";
            case "503": return "泥石流";
            case "504": return "岩溶塌陷";
            case "505": return "地裂缝";
            case "506": return "地面沉降";
            case "507": return "其他（沙土液化、火灾、毒气泄露、爆炸、环境污染、瘟疫、海啸等）";
            default: return "未知分类";
        }
    }

    private String decodeLabelFromCode(char categoryType, String labelCode) {
        switch (categoryType) {
            case '1': // 震情
                switch (labelCode) {
                    case "001": return "地理位置";
                    case "002": return "时间";
                    case "003": return "震级";
                    case "004": return "震源深度";
                    case "005": return "烈度";
                    default: return "未知指标";
                }
            case '2': // 人员伤亡及失踪
                switch (labelCode) {
                    case "001": return "受灾人数";
                    case "002": return "受灾程度";
                    default: return "未知指标";
                }
            case '3': // 房屋破坏
                switch (labelCode) {
                    case "001": return "一般损坏面积";
                    case "002": return "严重损坏面积";
                    case "003": return "受灾程度";
                    default: return "未知指标";
                }
            case '4': // 生命线工程灾情
                switch (labelCode) {
                    case "001": return "受灾设施数";
                    case "002": return "受灾范围";
                    case "003": return "受灾程度";
                    default: return "未知指标";
                }
            case '5': // 次生灾害
                switch (labelCode) {
                    case "001": return "灾害损失";
                    case "002": return "灾害范围";
                    case "003": return "受灾程度";
                    default: return "未知指标";
                }
            default:
                return "未知指标";
        }
    }
} 