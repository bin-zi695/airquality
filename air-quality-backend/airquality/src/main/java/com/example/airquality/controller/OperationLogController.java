package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.OperationLog;
import com.example.airquality.service.OperationLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<OperationLog>> list(@RequestParam(required = false) String action,
                                           @RequestParam(required = false) String username) {
        return Result.success(operationLogService.getList(action, username));
    }

    @GetMapping("/count")
    public Result<Long> count() {
        return Result.success(operationLogService.getCount());
    }
}
