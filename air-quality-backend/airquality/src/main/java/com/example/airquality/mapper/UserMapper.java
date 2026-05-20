package com.example.airquality.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.airquality.entity.User;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

    @Select("SELECT * FROM user WHERE role = #{role}")
    List<User> selectByRole(String role);

    @Select("<script>" +
            "SELECT * FROM user WHERE 1=1" +
            "<if test='username != null and username != \"\"'> AND username LIKE CONCAT('%',#{username},'%')</if>" +
            "<if test='role != null and role != \"\"'> AND role = #{role}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY id ASC" +
            "</script>")
    List<User> selectList(@Param("username") String username, @Param("role") String role, @Param("status") Integer status);

    @Insert("INSERT INTO user (username, password, nickname, email, phone, role, status) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{phone}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("<script>" +
            "UPDATE user SET updated_at = NOW()" +
            "<if test='password != null'> , password = #{password}</if>" +
            "<if test='nickname != null'> , nickname = #{nickname}</if>" +
            "<if test='email != null'> , email = #{email}</if>" +
            "<if test='phone != null'> , phone = #{phone}</if>" +
            "<if test='status != null'> , status = #{status}</if>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(User user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COALESCE(MAX(id), 0) FROM user")
    Long selectMaxId();
}
