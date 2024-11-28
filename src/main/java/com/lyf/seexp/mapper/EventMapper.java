package com.lyf.seexp.mapper;

import com.lyf.seexp.pojo.Event;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Delete("DELETE FROM event WHERE code = #{code}")
    void deleteByCode(@Param("code") String code);

    @Update("UPDATE event SET " +
            "location = #{event.location}, " +
            "time = #{event.time}, " +
            "source_category = #{event.sourceCategory}, " +
            "source_subcategory = #{event.sourceSubcategory}, " +
            "carrier = #{event.carrier}, " +
            "disaster_category = #{event.disasterCategory}, " +
            "disaster_subcategory = #{event.disasterSubcategory}, " +
            "disaster_indicator = #{event.disasterIndicator}, " +
            "description = #{event.description} " +
            "WHERE code = #{event.code}")
    void updateByCode(@Param("event") Event event);

    @Select("SELECT code, location, time, " +
            "source_category as sourceCategory, " +
            "source_subcategory as sourceSubcategory, " +
            "carrier, " +
            "disaster_category as disasterCategory, " +
            "disaster_subcategory as disasterSubcategory, " +
            "disaster_indicator as disasterIndicator, " +
            "description " +
            "FROM event " +
            "WHERE " +
            "code LIKE CONCAT('%', #{query}, '%') OR " +
            "location LIKE CONCAT('%', #{query}, '%') OR " +
            "source_category LIKE CONCAT('%', #{query}, '%') OR " +
            "source_subcategory LIKE CONCAT('%', #{query}, '%') OR " +
            "carrier LIKE CONCAT('%', #{query}, '%') OR " +
            "disaster_category LIKE CONCAT('%', #{query}, '%') OR " +
            "disaster_subcategory LIKE CONCAT('%', #{query}, '%') OR " +
            "disaster_indicator LIKE CONCAT('%', #{query}, '%') OR " +
            "description LIKE CONCAT('%', #{query}, '%') " +
            "ORDER BY time DESC")
    List<Event> searchEvents(@Param("query") String query);

}
