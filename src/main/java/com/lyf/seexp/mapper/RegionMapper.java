package com.lyf.seexp.mapper;


import com.lyf.seexp.pojo.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RegionMapper {
    //根据code查找地区字符串
    @Select("select * from region where code=#{code}")
    Region findByCode(String code);
}
