package com.example.airquality.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.airquality.entity.AirQualityArticle;

@Mapper
public interface AirQualityArticleMapper {

    @Select("SELECT * FROM air_quality_article WHERE id = #{id}")
    AirQualityArticle selectById(Long id);

    @Select("SELECT * FROM air_quality_article WHERE status = 1 ORDER BY id ASC")
    List<AirQualityArticle> selectPublished();

    @Select("<script>" +
            "SELECT * FROM air_quality_article WHERE 1=1" +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%',#{title},'%')</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY id ASC" +
            "</script>")
    List<AirQualityArticle> selectList(@Param("title") String title, @Param("status") Integer status);

    @Insert("INSERT INTO air_quality_article (title, content, summary, cover_image, author, status, publish_time) " +
            "VALUES (#{title}, #{content}, #{summary}, #{coverImage}, #{author}, #{status}, #{publishTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AirQualityArticle article);

    @Update("UPDATE air_quality_article SET title = #{title}, content = #{content}, summary = #{summary}, " +
            "cover_image = #{coverImage}, author = #{author}, status = #{status}, publish_time = #{publishTime}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int update(AirQualityArticle article);

    @Delete("DELETE FROM air_quality_article WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COALESCE(MAX(id), 0) FROM air_quality_article")
    Long selectMaxId();
}
