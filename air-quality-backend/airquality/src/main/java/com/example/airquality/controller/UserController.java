package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.User;
import com.example.airquality.service.OperationLogService;
import com.example.airquality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OperationLogService operationLogService;
    private final HttpServletRequest request;

    public UserController(UserService userService, OperationLogService operationLogService,
                          HttpServletRequest request) {
        this.userService = userService;
        this.operationLogService = operationLogService;
        this.request = request;
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    private void logAdminOp(Authentication auth, String action, String target, String detail) {
        Long userId = auth != null ? (Long) auth.getPrincipal() : 0L;
        String username = "";
        try { username = userService.getById(userId).getUsername(); } catch (Exception ignored) {}
        operationLogService.log(userId, username, action, target, detail, getClientIp());
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping
    public Result<List<User>> list(@RequestParam(required = false) String username,
                                    @RequestParam(required = false) String role,
                                    @RequestParam(required = false) Integer status) {
        return Result.success(userService.list(username, role, status));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user, Authentication auth) {
        user.setId(id);
        userService.update(user);
        logAdminOp(auth, "update", "用户管理", "更新用户信息: ID=" + id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status, Authentication auth) {
        userService.updateStatus(id, status);
        String detail = status == 1 ? "启用用户: ID=" + id : "禁用用户: ID=" + id;
        logAdminOp(auth, "update", "用户管理", detail);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        userService.deleteById(id);
        logAdminOp(auth, "delete", "用户管理", "删除用户: ID=" + id);
        return Result.success();
    }
}
