package com.example.airquality.mapper;

import com.example.airquality.entity.AlertThreshold;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AlertThresholdMapper {

    @Select("SELECT * FROM alert_threshold WHERE id = #{id}")
    AlertThreshold selectById(Long id);

    @Select("SELECT * FROM alert_threshold WHERE enabled = 1")
    List<AlertThreshold> selectEnabled();

    @Select("SELECT * FROM alert_threshold ORDER BY id ASC")
    List<AlertThreshold> selectAll();

    @Insert("INSERT INTO alert_threshold (threshold_name, aqi_threshold, pm25_threshold, pm10_threshold, enabled) " +
            "VALUES (#{thresholdName}, #{aqiThreshold}, #{pm25Threshold}, #{pm10Threshold}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AlertThreshold threshold);

    @Update("UPDATE alert_threshold SET threshold_name = #{thresholdName}, aqi_threshold = #{aqiThreshold}, " +
            "pm25_threshold = #{pm25Threshold}, pm10_threshold = #{pm10Threshold}, " +
            "enabled = #{enabled}, updated_at = NOW() WHERE id = #{id}")
    int update(AlertThreshold threshold);

    @Delete("DELETE FROM alert_threshold WHERE id = #{id}")
    int deleteById(Long id);
}
