package com.lyf.seexp.mapper;

import com.lyf.seexp.pojo.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RegionMapper {
    //根据code查找地区字符串
    @Select("select * from region where code=#{code}")
    Region findByCode(String code);

    //插入新的region记录
    @Insert("INSERT INTO region (code, name) VALUES (#{code}, #{name})")
    void insert(Region region);

    //更新已存在的region记录
    @Update("UPDATE region SET name = #{name} WHERE code = #{code}")
    void update(Region region);
}
