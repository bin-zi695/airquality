package com.example.airquality.service;

import com.example.airquality.entity.OperationLog;
import com.example.airquality.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogService(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    public void save(OperationLog log) {
        operationLogMapper.insert(log);
    }

    public List<OperationLog> list(Long userId, String operationType, String module,
                                    LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogMapper.selectList(userId, operationType, module, startTime, endTime);
    }
}
