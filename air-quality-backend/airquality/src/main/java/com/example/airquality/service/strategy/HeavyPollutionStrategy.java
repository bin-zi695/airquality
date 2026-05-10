package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;
import org.springframework.stereotype.Component;

@Component
public class HeavyPollutionStrategy implements AqiLevelStrategy {
    @Override
    public boolean supports(int aqi) {
        return aqi >= 201 && aqi <= 300;
    }

    @Override
    public AqiLevelResult getLevel() {
        return new AqiLevelResult(5, "重度污染", "#99004C", "心脏病和肺病患者症状显著加剧，运动耐受力降低，健康人群普遍出现症状");
    }
}
