package com.example.airquality.service;

import com.example.airquality.common.SqlUtils;
import com.example.airquality.entity.AlertThreshold;
import com.example.airquality.mapper.AlertThresholdMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertThresholdService {

    private final AlertThresholdMapper alertThresholdMapper;
    private final SqlUtils sqlUtils;

    public AlertThresholdService(AlertThresholdMapper alertThresholdMapper, SqlUtils sqlUtils) {
        this.alertThresholdMapper = alertThresholdMapper;
        this.sqlUtils = sqlUtils;
    }

    public AlertThreshold getById(Long id) {
        return alertThresholdMapper.selectById(id);
    }

    public List<AlertThreshold> getEnabled() {
        return alertThresholdMapper.selectEnabled();
    }

    public List<AlertThreshold> listAll() {
        return alertThresholdMapper.selectAll();
    }

    public void save(AlertThreshold threshold) {
        alertThresholdMapper.insert(threshold);
    }

    public void update(AlertThreshold threshold) {
        alertThresholdMapper.update(threshold);
    }

    public void deleteById(Long id) {
        alertThresholdMapper.deleteById(id);
        sqlUtils.resetAlertThresholdAutoIncrement(alertThresholdMapper.selectMaxId() + 1);
    }
}
