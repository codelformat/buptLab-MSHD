package com.lyf.seexp.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CodeInfo {
    private String location;            // 地点 (Location)

    @JsonDeserialize(using = DateDeserializers.TimestampDeserializer.class)
    private Timestamp time;             // 时间 (Time)
    private String sourceCategory;      // 来源大类 (Source Category)
    private String sourceSubcategory;   // 来源子类 (Source Subcategory)
    private String carrier;             // 载体 (Carrier)
    private String disasterCategory;    // 灾情大类 (Disaster Category)
    private String disasterSubcategory; // 灾情子类 (Disaster Subcategory)
    private String disasterIndicator;   // 灾情指标 (Disaster Indicator)
}
