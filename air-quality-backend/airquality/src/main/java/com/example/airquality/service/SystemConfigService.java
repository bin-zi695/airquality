package com.example.airquality.service;

import com.example.airquality.entity.SystemConfig;
import com.example.airquality.mapper.SystemConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    public SystemConfigService(SystemConfigMapper systemConfigMapper) {
        this.systemConfigMapper = systemConfigMapper;
    }

    public SystemConfig getByKey(String key) {
        return systemConfigMapper.selectByKey(key);
    }

    public String getValue(String key) {
        SystemConfig config = systemConfigMapper.selectByKey(key);
        return config != null ? config.getConfigValue() : null;
    }

    public void save(SystemConfig config) {
        systemConfigMapper.insert(config);
    }

    public void update(SystemConfig config) {
        systemConfigMapper.update(config);
    }
}
