package com.example.airquality.mapper;

import com.example.airquality.entity.UserFavorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {

    @Select("SELECT * FROM user_favorite WHERE id = #{id}")
    UserFavorite selectById(Long id);

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId}")
    List<UserFavorite> selectByUserId(Long userId);

    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND city_id = #{cityId}")
    UserFavorite selectByUserAndCity(@Param("userId") Long userId, @Param("cityId") Long cityId);

    @Select("SELECT city_id FROM user_favorite WHERE user_id = #{userId}")
    List<Long> selectCityIdsByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId}")
    int countByUserId(Long userId);

    @Insert("INSERT INTO user_favorite (user_id, city_id) VALUES (#{userId}, #{cityId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFavorite favorite);

    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND city_id = #{cityId}")
    int delete(@Param("userId") Long userId, @Param("cityId") Long cityId);
}
