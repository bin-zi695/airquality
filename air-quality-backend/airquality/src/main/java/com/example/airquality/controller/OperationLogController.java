package com.example.airquality.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.airquality.common.Result;
import com.example.airquality.entity.OperationLog;
import com.example.airquality.service.OperationLogService;

@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<OperationLog>> list(@RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) String operationType,
                                            @RequestParam(required = false) String module,
                                            @RequestParam(required = false) LocalDateTime startTime,
                                            @RequestParam(required = false) LocalDateTime endTime) {
        return Result.success(operationLogService.list(userId, operationType, module, startTime, endTime));
    }
}
