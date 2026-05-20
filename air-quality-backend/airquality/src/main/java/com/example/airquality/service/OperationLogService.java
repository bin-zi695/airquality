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

    public void log(Long userId, String username, String action, String target, String detail, String ip) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setAction(action);
        log.setTarget(target);
        log.setDetail(detail);
        log.setIp(ip);
        operationLogMapper.insert(log);
    }

    public List<OperationLog> getList(String action, String username) {
        return operationLogMapper.selectList(action, username);
    }

    public long getCount() {
        return operationLogMapper.selectCount();
    }

    public void cleanOlderThan(LocalDateTime date) {
        operationLogMapper.deleteOlderThan(date);
    }
}
