package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;
import org.springframework.stereotype.Component;

@Component
public class GoodLevelStrategy implements AqiLevelStrategy {
    @Override
    public boolean supports(int aqi) {
        return aqi >= 51 && aqi <= 100;
    }

    @Override
    public AqiLevelResult getLevel() {
        return new AqiLevelResult(2, "良", "#FFFF00", "空气质量可接受，但某些污染物可能对极少数异常敏感人群健康有较弱影响");
    }
}
