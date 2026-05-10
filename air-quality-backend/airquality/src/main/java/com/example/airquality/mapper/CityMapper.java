package com.example.airquality.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.airquality.entity.City;

@Mapper
public interface CityMapper {

    @Select("SELECT * FROM city WHERE id = #{id}")
    City selectById(Long id);

    @Select("SELECT * FROM city WHERE name = #{name}")
    City selectByName(String name);

    @Select("SELECT * FROM city ORDER BY id ASC")
    List<City> selectAll();

    @Select("<script>" +
            "SELECT * FROM city WHERE 1=1" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%',#{name},'%')</if>" +
            "<if test='province != null and province != \"\"'> AND province = #{province}</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY id ASC" +
            "</script>")
    List<City> selectList(@Param("name") String name, @Param("province") String province,
                          @Param("category") String category, @Param("status") Integer status);

    @Insert("INSERT INTO city (name, province, latitude, longitude, category, status) " +
            "VALUES (#{name}, #{province}, #{latitude}, #{longitude}, #{category}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(City city);

    @Update("UPDATE city SET name = #{name}, province = #{province}, latitude = #{latitude}, " +
            "longitude = #{longitude}, category = #{category}, status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int update(City city);

    @Delete("DELETE FROM city WHERE id = #{id}")
    int deleteById(Long id);
}
