package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.User;
import com.example.airquality.security.JwtUtil;
import com.example.airquality.service.OperationLogService;
import com.example.airquality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final OperationLogService operationLogService;
    private final HttpServletRequest request;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil, OperationLogService operationLogService,
                          HttpServletRequest request) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.operationLogService = operationLogService;
        this.request = request;
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
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

        operationLogService.log(user.getId(), username, "register", "用户注册",
                "新用户注册: " + username + " (" + email + ")", getClientIp());

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
        return Result.success(result);
    }
}
