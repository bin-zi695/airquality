package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.service.SystemConfigService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/{key}")
    public Result<String> getByKey(@PathVariable String key) {
        return Result.success(systemConfigService.getValue(key));
    }
}
