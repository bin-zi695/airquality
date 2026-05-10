package com.example.airquality.service;

import com.example.airquality.entity.AlertThreshold;
import com.example.airquality.mapper.AlertThresholdMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertThresholdService {

    private final AlertThresholdMapper alertThresholdMapper;

    public AlertThresholdService(AlertThresholdMapper alertThresholdMapper) {
        this.alertThresholdMapper = alertThresholdMapper;
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
    }
}
