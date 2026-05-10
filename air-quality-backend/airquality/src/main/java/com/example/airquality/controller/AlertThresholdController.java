package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.AlertThreshold;
import com.example.airquality.service.AlertThresholdService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alert-thresholds")
public class AlertThresholdController {

    private final AlertThresholdService alertThresholdService;

    public AlertThresholdController(AlertThresholdService alertThresholdService) {
        this.alertThresholdService = alertThresholdService;
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
    public Result<Void> save(@RequestBody AlertThreshold threshold) {
        alertThresholdService.save(threshold);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AlertThreshold threshold) {
        threshold.setId(id);
        alertThresholdService.update(threshold);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        alertThresholdService.deleteById(id);
        return Result.success();
    }
}
