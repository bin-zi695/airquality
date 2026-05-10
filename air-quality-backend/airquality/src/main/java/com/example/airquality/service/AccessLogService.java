package com.example.airquality.service;

import com.example.airquality.entity.AccessLog;
import com.example.airquality.mapper.AccessLogMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessLogService {

    private final AccessLogMapper accessLogMapper;

    public AccessLogService(AccessLogMapper accessLogMapper) {
        this.accessLogMapper = accessLogMapper;
    }

    public void save(AccessLog log) {
        accessLogMapper.insert(log);
    }

    public List<AccessLog> list(String requestUrl, LocalDateTime startTime, LocalDateTime endTime) {
        return accessLogMapper.selectList(requestUrl, startTime, endTime);
    }
}
