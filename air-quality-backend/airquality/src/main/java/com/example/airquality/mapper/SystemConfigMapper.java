package com.example.airquality.mapper;

import com.example.airquality.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SystemConfigMapper {

    @Select("SELECT * FROM system_config WHERE config_key = #{configKey}")
    SystemConfig selectByKey(String configKey);

    @Insert("INSERT INTO system_config (config_key, config_value, description) " +
            "VALUES (#{configKey}, #{configValue}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfig config);

    @Update("UPDATE system_config SET config_value = #{configValue}, " +
            "description = #{description}, updated_at = NOW() WHERE config_key = #{configKey}")
    int update(SystemConfig config);
}
