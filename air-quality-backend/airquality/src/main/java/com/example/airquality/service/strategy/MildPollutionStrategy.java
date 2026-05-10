package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;
import org.springframework.stereotype.Component;

@Component
public class MildPollutionStrategy implements AqiLevelStrategy {
    @Override
    public boolean supports(int aqi) {
        return aqi >= 101 && aqi <= 150;
    }

    @Override
    public AqiLevelResult getLevel() {
        return new AqiLevelResult(3, "轻度污染", "#FF7E00", "易感人群症状有轻度加剧，健康人群出现刺激症状");
    }
}
