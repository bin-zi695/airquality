package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.City;
import com.example.airquality.service.CityService;
import com.example.airquality.service.OperationLogService;
import com.example.airquality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;
    private final OperationLogService operationLogService;
    private final UserService userService;
    private final HttpServletRequest request;

    public CityController(CityService cityService, OperationLogService operationLogService,
                          UserService userService, HttpServletRequest request) {
        this.cityService = cityService;
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
    public Result<List<City>> list(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String province,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) Integer status) {
        return Result.success(cityService.list(name, province, category, status));
    }

    @GetMapping("/all")
    public Result<List<City>> listAll() {
        return Result.success(cityService.listAll());
    }

    @GetMapping("/{id}")
    public Result<City> getById(@PathVariable Long id) {
        return Result.success(cityService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody City city, Authentication auth) {
        cityService.save(city);
        logAdminOp(auth, "create", "城市管理", "新增城市: " + city.getName());
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody City city, Authentication auth) {
        city.setId(id);
        cityService.update(city);
        logAdminOp(auth, "update", "城市管理", "更新城市: ID=" + id + " " + city.getName());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        cityService.deleteById(id);
        logAdminOp(auth, "delete", "城市管理", "删除城市: ID=" + id);
        return Result.success();
    }
}
