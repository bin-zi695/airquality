package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.AlertThreshold;
import com.example.airquality.service.AlertThresholdService;
import com.example.airquality.service.OperationLogService;
import com.example.airquality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert-thresholds")
public class AlertThresholdController {

    private final AlertThresholdService alertThresholdService;
    private final OperationLogService operationLogService;
    private final UserService userService;
    private final HttpServletRequest request;

    public AlertThresholdController(AlertThresholdService alertThresholdService,
                                    OperationLogService operationLogService,
                                    UserService userService,
                                    HttpServletRequest request) {
        this.alertThresholdService = alertThresholdService;
        this.operationLogService = operationLogService;
        this.userService = userService;
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

    @GetMapping
    public Result<List<AlertThreshold>> listAll() {
        return Result.success(alertThresholdService.listAll());
    }

    @GetMapping("/enabled")
    public Result<List<AlertThreshold>> getEnabled() {
        return Result.success(alertThresholdService.getEnabled());
    }

    @GetMapping("/{id}")
    public Result<AlertThreshold> getById(@PathVariable Long id) {
        return Result.success(alertThresholdService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody AlertThreshold threshold, Authentication auth) {
        alertThresholdService.save(threshold);
        logAdminOp(auth, "create", "预警配置", "新增预警阈值: " + threshold.getThresholdName());
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AlertThreshold threshold, Authentication auth) {
        threshold.setId(id);
        alertThresholdService.update(threshold);
        logAdminOp(auth, "update", "预警配置", "更新预警阈值: ID=" + id + " " + threshold.getThresholdName());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        alertThresholdService.deleteById(id);
        logAdminOp(auth, "delete", "预警配置", "删除预警阈值: ID=" + id);
        return Result.success();
    }
}
