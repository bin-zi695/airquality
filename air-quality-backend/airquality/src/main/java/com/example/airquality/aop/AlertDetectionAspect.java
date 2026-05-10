package com.example.airquality.aop;

import com.example.airquality.entity.AirQualityData;
import com.example.airquality.entity.AlertThreshold;
import com.example.airquality.service.AlertThresholdService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class AlertDetectionAspect {

    private static final Logger log = LoggerFactory.getLogger(AlertDetectionAspect.class);
    private final AlertThresholdService alertThresholdService;

    public AlertDetectionAspect(AlertThresholdService alertThresholdService) {
        this.alertThresholdService = alertThresholdService;
    }

    @Pointcut("execution(* com.example.airquality.service.AirQualityDataService.save(..))")
    public void saveAirData() {}

    @Pointcut("execution(* com.example.airquality.service.AirQualityDataService.batchSave(..))")
    public void batchSaveAirData() {}

    @AfterReturning("saveAirData() && args(data)")
    public void afterSave(AirQualityData data) {
        checkAlert(data);
    }

    @AfterReturning("batchSaveAirData() && args(list)")
    public void afterBatchSave(List<AirQualityData> list) {
        if (list != null) {
            list.forEach(this::checkAlert);
        }
    }

    private void checkAlert(AirQualityData data) {
        if (data == null || data.getAqi() == null) return;
        List<AlertThreshold> thresholds = alertThresholdService.getEnabled();
        for (AlertThreshold t : thresholds) {
            if (data.getAqi() >= t.getAqiThreshold()) {
                log.warn("[空气质量预警] 城市ID={} 日期={} AQI={} 超过预警阈值{}，当前AQI等级为预警状态",
                        data.getCityId(), data.getDate(), data.getAqi(), t.getAqiThreshold());
                break;
            }
        }
    }
}
