package com.example.airquality.service;

import com.example.airquality.common.AqiLevelResult;
import com.example.airquality.service.strategy.AqiLevelStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AqiLevelService {

    private final List<AqiLevelStrategy> strategies;

    public AqiLevelService(List<AqiLevelStrategy> strategies) {
        this.strategies = strategies;
    }

    public AqiLevelResult determineLevel(int aqi) {
        for (AqiLevelStrategy strategy : strategies) {
            if (strategy.supports(aqi)) {
                return strategy.getLevel();
            }
        }
        return new AqiLevelResult(0, "未知", "#999999", "暂无判定信息");
    }
}
