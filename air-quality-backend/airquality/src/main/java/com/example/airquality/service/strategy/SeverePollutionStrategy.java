package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;
import org.springframework.stereotype.Component;

@Component
public class SeverePollutionStrategy implements AqiLevelStrategy {
    @Override
    public boolean supports(int aqi) {
        return aqi > 300;
    }

    @Override
    public AqiLevelResult getLevel() {
        return new AqiLevelResult(6, "严重污染", "#7E0023", "健康人群运动耐受力降低，有明显强烈症状，提前出现某些疾病");
    }
}
