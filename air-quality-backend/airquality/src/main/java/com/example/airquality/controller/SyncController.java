package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.service.DataSyncService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class SyncController {

    private final DataSyncService dataSyncService;

    public SyncController(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    @PostMapping("/sync-today")
    public Result<?> syncToday() {
        new Thread(() -> dataSyncService.syncTodayData()).start();
        return Result.success("今日数据同步已启动");
    }

    @PostMapping("/sync-yesterday")
    public Result<?> syncYesterday() {
        new Thread(() -> dataSyncService.syncYesterdayData()).start();
        return Result.success("昨日数据同步已启动");
    }

    @PostMapping("/sync-history")
    public Result<?> syncHistory() {
        new Thread(() -> {
            int count = dataSyncService.syncHistory10Days();
            System.out.println("历史数据同步完成: " + count + " 条");
        }).start();
        return Result.success("历史10天数据同步已启动");
    }
}
