package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.City;
import com.example.airquality.mapper.CityMapper;
import com.example.airquality.service.DataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class SyncController {

    private static final Logger log = LoggerFactory.getLogger(SyncController.class);
    private final DataSyncService dataSyncService;
    private final CityMapper cityMapper;

    public SyncController(DataSyncService dataSyncService, CityMapper cityMapper) {
        this.dataSyncService = dataSyncService;
        this.cityMapper = cityMapper;
    }

    @GetMapping("/sync-now")
    public Result<?> syncNowGet() {
        log.info("=== GET 手动触发数据同步 ===");
        return doSync();
    }

    @PostMapping("/sync-now")
    public Result<?> syncNowPost() {
        log.info("=== POST 手动触发数据同步 ===");
        return doSync();
    }

    @GetMapping("/sync-history")
    public Result<?> syncHistory(@RequestParam(defaultValue = "3") int days) {
        log.info("=== 回填最近{}天历史数据 ===", days);
        new Thread(() -> dataSyncService.syncHistory(days)).start();
        return Result.success("历史数据回填已启动, 后台运行中，请查看日志");
    }

    private Result<?> doSync() {
        LocalDate today = LocalDate.now();
        List<City> cities = cityMapper.selectAll();
        int success = 0, fail = 0, skip = 0;
        StringBuilder detail = new StringBuilder();

        for (City city : cities) {
            try {
                if (city.getLatitude() == null || city.getLongitude() == null) {
                    skip++;
                    detail.append(city.getName()).append(": 无经纬度; ");
                    continue;
                }
                if (dataSyncService.syncCityNow(city, today)) {
                    success++;
                } else {
                    fail++;
                    detail.append(city.getName()).append(": 同步失败; ");
                }
                Thread.sleep(300);
            } catch (Exception e) {
                fail++;
                detail.append(city.getName()).append(": ").append(e.getMessage()).append("; ");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", cities.size());
        result.put("success", success);
        result.put("fail", fail);
        result.put("skip", skip);
        result.put("detail", detail.toString());
        log.info("手动同步完成: 成功{} 失败{} 跳过{}", success, fail, skip);
        return Result.success(result);
    }
}
