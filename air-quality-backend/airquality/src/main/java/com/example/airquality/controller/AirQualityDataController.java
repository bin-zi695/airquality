package com.example.airquality.controller;

import java.time.LocalDate;
import java.util.List;

import com.example.airquality.service.OperationLogService;
import com.example.airquality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.airquality.common.Result;
import com.example.airquality.entity.AirQualityData;
import com.example.airquality.service.AirQualityDataService;
import com.example.airquality.service.AqiLevelService;

@RestController
@RequestMapping("/api/air-data")
public class AirQualityDataController {

    private final AirQualityDataService airQualityDataService;
    private final AqiLevelService aqiLevelService;
    private final OperationLogService operationLogService;
    private final UserService userService;
    private final HttpServletRequest request;

    public AirQualityDataController(AirQualityDataService airQualityDataService,
                                     AqiLevelService aqiLevelService,
                                     OperationLogService operationLogService,
                                     UserService userService,
                                     HttpServletRequest request) {
        this.airQualityDataService = airQualityDataService;
        this.aqiLevelService = aqiLevelService;
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

    @GetMapping("/{id}")
    public Result<AirQualityData> getById(@PathVariable Long id) {
        return Result.success(airQualityDataService.getById(id));
    }

    @GetMapping
    public Result<List<AirQualityData>> list(@RequestParam(required = false) Long cityId,
                                              @RequestParam(required = false) LocalDate startDate,
                                              @RequestParam(required = false) LocalDate endDate) {
        return Result.success(airQualityDataService.list(cityId, startDate, endDate));
    }

    @GetMapping("/latest")
    public Result<AirQualityData> getLatestByCity(@RequestParam Long cityId) {
        return Result.success(airQualityDataService.getLatestByCity(cityId));
    }

    @GetMapping("/favorites-latest")
    public Result<List<AirQualityData>> getLatestByFavorites(@RequestParam Long userId) {
        return Result.success(airQualityDataService.getLatestByFavorites(userId));
    }

    @GetMapping("/favorites-dates")
    public Result<?> getFavoriteDates(@RequestParam Long userId) {
        return Result.success(airQualityDataService.getFavoriteDates(userId));
    }

    @GetMapping("/favorites-by-date")
    public Result<List<AirQualityData>> getFavoritesByDate(@RequestParam Long userId,
                                                            @RequestParam LocalDate date) {
        return Result.success(airQualityDataService.getFavoritesByDate(userId, date));
    }

    @GetMapping("/trend")
    public Result<List<AirQualityData>> getTrend(@RequestParam Long cityId,
                                                  @RequestParam LocalDate startDate,
                                                  @RequestParam LocalDate endDate) {
        return Result.success(airQualityDataService.getTrend(cityId, startDate, endDate));
    }

    @GetMapping("/all-dates")
    public Result<?> getAllDates() {
        return Result.success(airQualityDataService.getAllDates());
    }

    @GetMapping("/all-by-date")
    public Result<List<AirQualityData>> getAllByDate(@RequestParam LocalDate date) {
        return Result.success(airQualityDataService.getAllByDate(date));
    }

    @GetMapping("/aqi-level/{aqi}")
    public Result<?> getAqiLevel(@PathVariable int aqi) {
        return Result.success(aqiLevelService.determineLevel(aqi));
    }

    @PostMapping
    public Result<Void> save(@RequestBody AirQualityData data, Authentication auth) {
        airQualityDataService.save(data);
        logAdminOp(auth, "create", "数据管理", "新增空气质量数据: 城市ID=" + data.getCityId() + " 日期=" + data.getDate());
        return Result.success();
    }

    @PostMapping("/batch")
    public Result<Void> batchSave(@RequestBody List<AirQualityData> list, Authentication auth) {
        airQualityDataService.batchSave(list);
        logAdminOp(auth, "create", "数据管理", "批量采集数据: " + list.size() + "条");
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AirQualityData data, Authentication auth) {
        data.setId(id);
        airQualityDataService.update(data);
        logAdminOp(auth, "update", "数据管理", "更新空气质量数据: ID=" + id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        airQualityDataService.deleteById(id);
        logAdminOp(auth, "delete", "数据管理", "删除空气质量数据: ID=" + id);
        return Result.success();
    }

    @DeleteMapping("/city/{cityId}")
    public Result<Void> deleteByCityId(@PathVariable Long cityId, Authentication auth) {
        airQualityDataService.deleteByCityId(cityId);
        logAdminOp(auth, "delete", "数据管理", "删除城市空气质量数据: 城市ID=" + cityId);
        return Result.success();
    }
}
