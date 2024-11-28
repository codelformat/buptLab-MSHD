package com.lyf.seexp.mapper;

import com.lyf.seexp.pojo.Event;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface EventMapper {


    // 添加条目
    @Insert("INSERT INTO event(code, location, time, source_category, source_subcategory, carrier, disaster_category, disaster_subcategory, disaster_indicator, description) " +
            "VALUES (#{event.code}, #{event.location}, #{event.time}, #{event.sourceCategory}, #{event.sourceSubcategory}, #{event.carrier}, #{event.disasterCategory}, #{event.disasterSubcategory}, #{event.disasterIndicator}, #{event.description})")
    void add(@Param("event") Event event);

    // 添加查询所有事件的方法
    @Select("SELECT code, location, time, " +
            "source_category as sourceCategory, " +
            "source_subcategory as sourceSubcategory, " +
            "carrier, " +
            "disaster_category as disasterCategory, " +
            "disaster_subcategory as disasterSubcategory, " +
            "disaster_indicator as disasterIndicator, " +
            "description " +
            "FROM event ORDER BY time DESC")
    List<Event> findAll();

}
