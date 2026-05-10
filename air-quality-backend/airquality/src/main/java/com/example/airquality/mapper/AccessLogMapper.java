package com.example.airquality.mapper;

import com.example.airquality.entity.AccessLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AccessLogMapper {

    @Insert("INSERT INTO access_log (user_id, username, ip_address, request_method, request_url, " +
            "request_params, response_status, execution_time, user_agent, created_at) " +
            "VALUES (#{userId}, #{username}, #{ipAddress}, #{requestMethod}, #{requestUrl}, " +
            "#{requestParams}, #{responseStatus}, #{executionTime}, #{userAgent}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AccessLog log);

    @Select("<script>" +
            "SELECT * FROM access_log WHERE 1=1" +
            "<if test='requestUrl != null and requestUrl != \"\"'> AND request_url LIKE CONCAT('%',#{requestUrl},'%')</if>" +
            "<if test='startTime != null'> AND created_at &gt;= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            " ORDER BY id ASC" +
            "</script>")
    List<AccessLog> selectList(@Param("requestUrl") String requestUrl,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);
}
