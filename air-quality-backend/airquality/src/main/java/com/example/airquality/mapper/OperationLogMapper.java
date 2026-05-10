package com.example.airquality.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.airquality.entity.OperationLog;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log (user_id, username, operation_type, module, description, " +
            "request_method, request_url, request_params, ip_address, status, error_msg, execution_time, operation_time) " +
            "VALUES (#{userId}, #{username}, #{operationType}, #{module}, #{description}, " +
            "#{requestMethod}, #{requestUrl}, #{requestParams}, #{ipAddress}, #{status}, #{errorMsg}, #{executionTime}, #{operationTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Select("<script>" +
            "SELECT * FROM operation_log WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='operationType != null and operationType != \"\"'> AND operation_type = #{operationType}</if>" +
            "<if test='module != null and module != \"\"'> AND module = #{module}</if>" +
            "<if test='startTime != null'> AND operation_time &gt;= #{startTime}</if>" +
            "<if test='endTime != null'> AND operation_time &lt;= #{endTime}</if>" +
            " ORDER BY id ASC" +
            "</script>")
    List<OperationLog> selectList(@Param("userId") Long userId,
                                   @Param("operationType") String operationType,
                                   @Param("module") String module,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);
}
