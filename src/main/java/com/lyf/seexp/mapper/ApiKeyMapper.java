package com.lyf.seexp.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import com.lyf.seexp.pojo.ApiKey;

@Mapper
public interface ApiKeyMapper {
    @Select("SELECT * FROM api_key WHERE api_key = #{apiKey} LIMIT 1")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "api_key", property = "apiKey"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "expires_at", property = "expiresAt"),
            @Result(column = "is_active", property = "isActive"),
            @Result(column = "is_permanent", property = "isPermanent")
    })
    ApiKey selectByApiKey(@Param("apiKey") String apiKey);

    @Insert("INSERT INTO api_key(user_id,api_key,created_at,expires_at,is_active) VALUES (#{userId},#{apiKey},#{createdAt},#{expiresAt},#{isActive})")
    void insert(@Param("apiKey") ApiKey apiKey);
}
