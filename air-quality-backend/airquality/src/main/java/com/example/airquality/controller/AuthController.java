package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.User;
import com.example.airquality.security.JwtUtil;
import com.example.airquality.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        if (username == null || username.isBlank()) {
            return Result.error(400, "用户名不能为空");
        }
        if (email == null || email.isBlank()) {
            return Result.error(400, "邮箱不能为空");
        }
        User exist = userService.getByEmail(email);
        if (exist != null) {
            return Result.error(400, "邮箱已注册");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNickname(username);
        userService.register(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        if (email == null || email.isBlank()) {
            return Result.error(400, "邮箱不能为空");
        }
        User user = userService.getByEmail(email);
        if (user == null) {
            return Result.error(400, "账号不存在");
        }
        if (user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error(400, "密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), email, user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", email);
        result.put("nickname", user.getNickname());
        result.put("role", user.getRole());
        result.put("avatar", user.getAvatar());
        return Result.success(result);
    }
}
