package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;
import org.springframework.stereotype.Component;

@Component
public class ExcellentLevelStrategy implements AqiLevelStrategy {
    @Override
    public boolean supports(int aqi) {
        return aqi >= 0 && aqi <= 50;
    }

    @Override
    public AqiLevelResult getLevel() {
        return new AqiLevelResult(1, "优", "#00E400", "空气质量令人满意，基本无空气污染");
    }
}
