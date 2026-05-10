package com.example.airquality.service.strategy;

import com.example.airquality.common.AqiLevelResult;

public interface AqiLevelStrategy {
    boolean supports(int aqi);
    AqiLevelResult getLevel();
}
