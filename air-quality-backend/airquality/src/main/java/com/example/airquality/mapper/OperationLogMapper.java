package com.example.airquality.mapper;

import com.example.airquality.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    OperationLog selectById(Long id);

    List<OperationLog> selectList(@Param("action") String action,
                                  @Param("username") String username);

    int insert(OperationLog log);

    int deleteOlderThan(LocalDateTime date);

    long selectCount();
}
