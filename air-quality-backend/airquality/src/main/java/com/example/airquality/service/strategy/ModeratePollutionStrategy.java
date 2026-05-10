package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;
import org.springframework.stereotype.Component;

@Component
public class ModeratePollutionStrategy implements AqiLevelStrategy {
    @Override
    public boolean supports(int aqi) {
        return aqi >= 151 && aqi <= 200;
    }

    @Override
    public AqiLevelResult getLevel() {
        return new AqiLevelResult(4, "中度污染", "#FF0000", "进一步加剧易感人群症状，可能对健康人群心脏、呼吸系统有影响");
    }
}
