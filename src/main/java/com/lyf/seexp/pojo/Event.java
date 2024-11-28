package com.lyf.seexp.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Event {
    private String code;                // 编码 (Code)
    private String location;            // 地点 (Location)
    private Timestamp time;             // 时间 (Time)
    private String sourceCategory;      // 来源大类 (Source Category)
    private String sourceSubcategory;   // 来源子类 (Source Subcategory)
    private String carrier;             // 载体 (Carrier)
    private String disasterCategory;    // 灾情大类 (Disaster Category)
    private String disasterSubcategory; // 灾情子类 (Disaster Subcategory)
    private String disasterIndicator;   // 灾情指标 (Disaster Indicator)
    private String description;         // 描述 (Description)

    public void setDescription(String description) {
        this.description = description;
    }
}
