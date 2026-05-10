package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.AccessLog;
import com.example.airquality.service.AccessLogService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/access-logs")
public class AccessLogController {

    private final AccessLogService accessLogService;

    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @GetMapping
    public Result<List<AccessLog>> list(@RequestParam(required = false) String requestUrl,
                                         @RequestParam(required = false) LocalDateTime startTime,
                                         @RequestParam(required = false) LocalDateTime endTime) {
        return Result.success(accessLogService.list(requestUrl, startTime, endTime));
    }
}
