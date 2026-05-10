package com.example.airquality.entity;

import java.time.LocalDateTime;

public class AlertThreshold {
    private Long id;
    private String thresholdName;
    private Integer aqiThreshold;
    private Double pm25Threshold;
    private Double pm10Threshold;
    private Integer enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getThresholdName() { return thresholdName; }
    public void setThresholdName(String thresholdName) { this.thresholdName = thresholdName; }

    public Integer getAqiThreshold() { return aqiThreshold; }
    public void setAqiThreshold(Integer aqiThreshold) { this.aqiThreshold = aqiThreshold; }

    public Double getPm25Threshold() { return pm25Threshold; }
    public void setPm25Threshold(Double pm25Threshold) { this.pm25Threshold = pm25Threshold; }

    public Double getPm10Threshold() { return pm10Threshold; }
    public void setPm10Threshold(Double pm10Threshold) { this.pm10Threshold = pm10Threshold; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
