package com.example.airquality.service;

import com.example.airquality.entity.AirQualityData;
import com.example.airquality.entity.City;
import com.example.airquality.mapper.AirQualityDataMapper;
import com.example.airquality.mapper.CityMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DataSyncService {

    private static final Logger log = LoggerFactory.getLogger(DataSyncService.class);
    private final HeFengApiService heFengApiService;
    private final CityMapper cityMapper;
    private final AirQualityDataMapper airQualityDataMapper;

    public DataSyncService(HeFengApiService heFengApiService,
                           CityMapper cityMapper,
                           AirQualityDataMapper airQualityDataMapper) {
        this.heFengApiService = heFengApiService;
        this.cityMapper = cityMapper;
        this.airQualityDataMapper = airQualityDataMapper;
    }

    public void syncLatestForAllCities() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        List<City> cities = cityMapper.selectAll();
        for (City city : cities) {
            try {
                syncCityDate(city, yesterday);
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步昨日数据失败: {} {}", city.getName(), e.getMessage());
            }
        }
        for (City city : cities) {
            try {
                syncCityDate(city, today);
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步今日数据失败: {} {}", city.getName(), e.getMessage());
            }
        }
    }

    public void syncTodayData() {
        LocalDate today = LocalDate.now();
        List<City> cities = cityMapper.selectAll();
        for (City city : cities) {
            try {
                syncCityDate(city, today);
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步今日数据失败: {} {}", city.getName(), e.getMessage());
            }
        }
    }

    public void syncYesterdayData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<City> cities = cityMapper.selectAll();
        for (City city : cities) {
            try {
                syncCityDate(city, yesterday);
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步昨日数据失败: {} {}", city.getName(), e.getMessage());
            }
        }
    }

    public int syncHistory10Days() {
        int total = 0;
        List<City> cities = cityMapper.selectAll();
        for (City city : cities) {
            for (int i = 1; i <= 10; i++) {
                LocalDate d = LocalDate.now().minusDays(i);
                try {
                    boolean ok = syncCityDate(city, d);
                    if (ok) total++;
                    Thread.sleep(300);
                } catch (Exception e) {
                    log.warn("同步历史数据失败: {} {} {}", city.getName(), d, e.getMessage());
                }
            }
        }
        log.info("历史数据同步完成，共入库 {} 条", total);
        return total;
    }

    public boolean syncCityDate(City city, LocalDate date) {
        if (city.getLocationId() == null) return false;
        try {
            JsonNode root = heFengApiService.getHistoricalAirQuality(city.getLocationId(), date.toString());
            if (root == null || !"200".equals(root.path("code").asText())) {
                log.debug("API返回异常: {} {} code={}", city.getName(), date, root != null ? root.path("code").asText() : "null");
                return false;
            }

            JsonNode dataNode = root.path("now");
            if (dataNode.isMissingNode() || dataNode.isNull()) return false;

            AirQualityData record = parseNode(dataNode);
            if (record == null) return false;

            record.setCityId(city.getId());
            record.setDate(date);

            AirQualityData exist = airQualityDataMapper.selectByCityAndDate(city.getId(), date);
            if (exist != null) {
                record.setId(exist.getId());
                airQualityDataMapper.update(record);
            } else {
                airQualityDataMapper.insert(record);
            }
            return true;
        } catch (Exception e) {
            log.error("同步异常: {} {}", city.getName(), e.getMessage());
            return false;
        }
    }

    private AirQualityData parseNode(JsonNode n) {
        if (n == null || n.isMissingNode()) return null;
        AirQualityData d = new AirQualityData();
        d.setAqi(n.path("aqi").asInt());
        d.setPm25(n.has("pm2p5") && !n.get("pm2p5").isNull() ? n.get("pm2p5").asDouble() : null);
        d.setPm10(n.has("pm10") && !n.get("pm10").isNull() ? n.get("pm10").asDouble() : null);
        d.setSo2(n.has("so2") && !n.get("so2").isNull() ? n.get("so2").asDouble() : null);
        d.setNo2(n.has("no2") && !n.get("no2").isNull() ? n.get("no2").asDouble() : null);
        d.setCo(n.has("co") && !n.get("co").isNull() ? n.get("co").asDouble() : null);
        d.setO3(n.has("o3") && !n.get("o3").isNull() ? n.get("o3").asDouble() : null);
        return d;
    }
}
