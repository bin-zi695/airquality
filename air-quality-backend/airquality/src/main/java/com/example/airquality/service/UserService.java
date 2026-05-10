package com.example.airquality.service;

import com.example.airquality.common.Result;
import com.example.airquality.entity.User;
import com.example.airquality.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public User getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    public List<User> list(String username, String role, Integer status) {
        return userMapper.selectList(username, role, status);
    }

    public void register(User user) {
        user.setRole("user");
        user.setStatus(1);
        if (user.getNickname() == null || user.getNickname().isBlank()) {
            user.setNickname(user.getUsername());
        }
        userMapper.insert(user);
    }

    public void update(User user) {
        userMapper.update(user);
    }

    public void changePassword(Long userId, String newPassword) {
        User user = new User();
        user.setId(userId);
        user.setPassword(newPassword);
        userMapper.update(user);
    }

    public void updateStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
}
