package com.example.airquality.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.airquality.entity.AirQualityData;

@Mapper
public interface AirQualityDataMapper {

    @Select("SELECT * FROM air_quality_data WHERE id = #{id}")
    AirQualityData selectById(Long id);

    @Select("SELECT * FROM air_quality_data WHERE city_id = #{cityId} AND date = #{date}")
    AirQualityData selectByCityAndDate(@Param("cityId") Long cityId, @Param("date") LocalDate date);

    @Select("<script>" +
            "SELECT a.* FROM air_quality_data a WHERE 1=1" +
            "<if test='cityId != null'> AND a.city_id = #{cityId}</if>" +
            "<if test='startDate != null'> AND a.date &gt;= #{startDate}</if>" +
            "<if test='endDate != null'> AND a.date &lt;= #{endDate}</if>" +
            " ORDER BY a.date DESC, a.id DESC" +
            "</script>")
    List<AirQualityData> selectList(@Param("cityId") Long cityId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    @Select("SELECT a.* FROM air_quality_data a " +
            "INNER JOIN user_favorite uf ON a.city_id = uf.city_id " +
            "WHERE uf.user_id = #{userId} " +
            "AND a.id IN (SELECT MAX(b.id) FROM air_quality_data b GROUP BY b.city_id) " +
            "ORDER BY a.city_id ASC")
    List<AirQualityData> selectLatestByFavorites(Long userId);

    @Select("SELECT DISTINCT a.date FROM air_quality_data a " +
            "INNER JOIN user_favorite uf ON a.city_id = uf.city_id " +
            "WHERE uf.user_id = #{userId} ORDER BY a.date DESC")
    List<java.time.LocalDate> selectFavoriteDates(Long userId);

    @Select("SELECT a.* FROM air_quality_data a " +
            "INNER JOIN user_favorite uf ON a.city_id = uf.city_id " +
            "WHERE uf.user_id = #{userId} AND a.date = #{date} " +
            "ORDER BY a.city_id ASC")
    List<AirQualityData> selectFavoritesByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Select("SELECT * FROM air_quality_data WHERE date = (SELECT MAX(date) FROM air_quality_data WHERE city_id = #{cityId}) AND city_id = #{cityId}")
    AirQualityData selectLatestByCity(Long cityId);

    @Select("SELECT * FROM air_quality_data WHERE date BETWEEN #{startDate} AND #{endDate} AND city_id = #{cityId} ORDER BY date ASC")
    List<AirQualityData> selectTrend(@Param("cityId") Long cityId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Select("SELECT * FROM air_quality_data WHERE city_id = #{cityId} ORDER BY date DESC")
    List<AirQualityData> selectByCity(@Param("cityId") Long cityId);

    @Select("SELECT DISTINCT date FROM air_quality_data ORDER BY date DESC")
    List<java.time.LocalDate> selectAllDates();

    @Select("SELECT * FROM air_quality_data " +
            "WHERE date = #{date} AND aqi IS NOT NULL ORDER BY city_id ASC")
    List<AirQualityData> selectAllByDate(@Param("date") LocalDate date);

    @Insert("INSERT INTO air_quality_data (city_id, date, aqi, pm25, pm10, so2, no2, co, o3, " +
            "temperature, humidity, wind_direction, wind_speed, weather, created_at) VALUES " +
            "(#{cityId}, #{date}, #{aqi}, #{pm25}, #{pm10}, #{so2}, #{no2}, #{co}, #{o3}, " +
            "#{temperature}, #{humidity}, #{windDirection}, #{windSpeed}, #{weather}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AirQualityData data);

    @Update("UPDATE air_quality_data SET aqi = #{aqi}, pm25 = #{pm25}, pm10 = #{pm10}, " +
            "so2 = #{so2}, no2 = #{no2}, co = #{co}, o3 = #{o3}, " +
            "temperature = #{temperature}, humidity = #{humidity}, " +
            "wind_direction = #{windDirection}, wind_speed = #{windSpeed}, weather = #{weather}, " +
            "created_at = NOW() " +
            "WHERE id = #{id}")
    int update(AirQualityData data);

    @Delete("DELETE FROM air_quality_data WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM air_quality_data WHERE city_id = #{cityId}")
    int deleteByCityId(Long cityId);

    @Insert("<script>" +
            "INSERT INTO air_quality_data (city_id, date, aqi, pm25, pm10, so2, no2, co, o3, " +
            "temperature, humidity, wind_direction, wind_speed, weather, created_at) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.cityId}, #{item.date}, #{item.aqi}, #{item.pm25}, #{item.pm10}, " +
            "#{item.so2}, #{item.no2}, #{item.co}, #{item.o3}, #{item.temperature}, " +
            "#{item.humidity}, #{item.windDirection}, #{item.windSpeed}, #{item.weather}, NOW())" +
            "</foreach>" +
            " ON DUPLICATE KEY UPDATE aqi = VALUES(aqi), pm25 = VALUES(pm25), pm10 = VALUES(pm10), " +
            "so2 = VALUES(so2), no2 = VALUES(no2), co = VALUES(co), o3 = VALUES(o3), " +
            "temperature = VALUES(temperature), humidity = VALUES(humidity), " +
            "wind_direction = VALUES(wind_direction), wind_speed = VALUES(wind_speed), weather = VALUES(weather), " +
            "created_at = NOW()" +
            "</script>")
    int batchInsert(List<AirQualityData> list);

    @Delete("DELETE FROM air_quality_data WHERE date < #{date}")
    int deleteOlderThan(LocalDate date);

    @Select("SELECT COALESCE(MAX(id), 0) FROM air_quality_data")
    Long selectMaxId();
}
