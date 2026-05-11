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

    public void syncTodayData() {
        log.info("开始同步今日所有城市空气质量数据...");
        syncDate(LocalDate.now());
    }

    public void syncYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String dateStr = yesterday.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        log.info("开始同步昨日({})所有城市数据(使用时光机API)...", yesterday);
        List<City> cities = cityMapper.selectAll();
        int success = 0, fail = 0;
        for (City city : cities) {
            try {
                if (backfillSingleDay(city, yesterday, dateStr)) success++;
                else fail++;
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步异常: {} {}", city.getName(), e.getMessage());
                fail++;
            }
        }
        log.info("昨日同步完成: 成功{} 失败{} (共{}城市)", success, fail, cities.size());
    }

    private void syncDate(LocalDate date) {
        List<City> cities = cityMapper.selectAll();
        int success = 0, fail = 0;
        for (City city : cities) {
            try {
                if (syncCityNow(city, date)) success++;
                else fail++;
                Thread.sleep(300);
            } catch (Exception e) {
                log.warn("同步异常: {} {}", city.getName(), e.getMessage());
                fail++;
            }
        }
        log.info("{}同步完成: 成功{} 失败{} (共{}城市)", date, success, fail, cities.size());
    }

    public boolean syncCityNow(City city, LocalDate date) {
        if (city.getLatitude() == null || city.getLongitude() == null) {
            log.warn("{} 无经纬度, 跳过", city.getName());
            return false;
        }
        try {
            log.info("开始同步城市: {} (lat={}, lng={})", city.getName(), city.getLatitude(), city.getLongitude());
            JsonNode root = heFengApiService.getNowAirQuality(city.getLatitude(), city.getLongitude());
            if (root == null) {
                log.warn("{} API返回null", city.getName());
                return false;
            }

            JsonNode indexes = root.path("indexes");
            if (indexes.isMissingNode() || !indexes.isArray() || indexes.size() == 0) {
                log.warn("{} API返回中无indexes数据", city.getName());
                return false;
            }

            int aqi = 0;
            JsonNode pollutantsNode = root.path("pollutants");

            for (JsonNode idx : indexes) {
                if ("cn-mee".equals(idx.path("code").asText())) {
                    aqi = idx.path("aqi").asInt(0);
                    break;
                }
            }
            if (aqi == 0 && indexes.size() > 0) {
                aqi = indexes.get(0).path("aqi").asInt(0);
            }

            AirQualityData record = new AirQualityData();
            record.setAqi(aqi);

            if (pollutantsNode.isArray()) {
                for (JsonNode p : pollutantsNode) {
                    String code = p.path("code").asText();
                    JsonNode conc = p.path("concentration");
                    double val = conc.path("value").asDouble(0);
                    switch (code) {
                        case "pm2p5": record.setPm25(val); break;
                        case "pm10": record.setPm10(val); break;
                        case "so2": record.setSo2(val); break;
                        case "no2": record.setNo2(val); break;
                        case "co": record.setCo(val); break;
                        case "o3": record.setO3(val); break;
                    }
                }
            }

            try {
                JsonNode weather = heFengApiService.getNowWeather(city.getLatitude(), city.getLongitude());
                if (weather != null && "200".equals(weather.path("code").asText())) {
                    JsonNode now = weather.path("now");
                    if (!now.isMissingNode()) {
                        record.setTemperature(parseDouble(now, "temp"));
                        record.setHumidity(parseDouble(now, "humidity"));
                        record.setWindDirection(now.path("windDir").asText(null));
                        record.setWindSpeed(now.path("windSpeed").asText(null));
                        record.setWeather(now.path("text").asText(null));
                        log.info("{} 天气: temp={} humidity={} wind={} {}",
                                city.getName(), record.getTemperature(), record.getHumidity(),
                                record.getWindDirection(), record.getWeather());
                    }
                }
            } catch (Exception e) {
                log.warn("{} 天气数据获取失败: {}", city.getName(), e.getMessage());
            }

            record.setCityId(city.getId());
            record.setDate(date);

            if (record.getAqi() == 0 && record.getPm25() == null) {
                log.warn("{} 空气质量数据全为空, 跳过", city.getName());
                return false;
            }

            AirQualityData exist = airQualityDataMapper.selectByCityAndDate(city.getId(), date);
            if (exist != null) {
                record.setId(exist.getId());
                airQualityDataMapper.update(record);
                log.info("{} 更新成功 AQI={} PM2.5={}", city.getName(), record.getAqi(), record.getPm25());
            } else {
                airQualityDataMapper.insert(record);
                log.info("{} 入库成功 AQI={} PM2.5={}", city.getName(), record.getAqi(), record.getPm25());
            }
            return true;
        } catch (Exception e) {
            log.error("{} 同步失败: {}", city.getName(), e.getMessage(), e);
            return false;
        }
    }

    private Double parseDouble(JsonNode parent, String field) {
        JsonNode node = parent.get(field);
        if (node == null || node.isNull()) return null;
        try {
            return node.asDouble();
        } catch (Exception e) {
            return null;
        }
    }

    public void syncHistory(int days) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(days);
        int totalSuccess = 0, totalFail = 0;

        for (LocalDate date = start; date.isBefore(today); date = date.plusDays(1)) {
            String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            log.info("========== 回填历史数据: {} ==========", date);
            for (City city : cityMapper.selectAll()) {
                try {
                    if (backfillSingleDay(city, date, dateStr)) totalSuccess++;
                    else totalFail++;
                    Thread.sleep(200);
                } catch (Exception e) {
                    log.warn("{} {} 回填异常: {}", city.getName(), date, e.getMessage());
                    totalFail++;
                }
            }
        }
        log.info("历史回填完成: 成功{} 失败{}", totalSuccess, totalFail);
    }

    private boolean backfillSingleDay(City city, LocalDate date, String dateStr) {
        if (city.getLocationId() == null || city.getLocationId().isBlank()) {
            return false;
        }
        try {
            JsonNode airRoot = heFengApiService.getHistoricalAir(city.getLocationId(), dateStr);
            if (airRoot == null || !"200".equals(airRoot.path("code").asText())) {
                log.debug("{} {} 历史空气质量无数据", city.getName(), date);
                return false;
            }

            JsonNode airHourly = airRoot.path("airHourly");
            if (!airHourly.isArray() || airHourly.size() == 0) return false;

            JsonNode latest = airHourly.get(airHourly.size() - 1);

            AirQualityData record = new AirQualityData();
            record.setCityId(city.getId());
            record.setDate(date);
            record.setAqi(parseInt(latest, "aqi"));
            record.setPm25(parseDouble(latest, "pm2p5"));
            record.setPm10(parseDouble(latest, "pm10"));
            record.setSo2(parseDouble(latest, "so2"));
            record.setNo2(parseDouble(latest, "no2"));
            record.setCo(parseDouble(latest, "co"));
            record.setO3(parseDouble(latest, "o3"));

            if (record.getAqi() == null && record.getPm25() == null) return false;

            try {
                JsonNode weatherRoot = heFengApiService.getHistoricalWeather(city.getLocationId(), dateStr);
                if (weatherRoot != null && "200".equals(weatherRoot.path("code").asText())) {
                    JsonNode daily = weatherRoot.path("weatherDaily");
                    if (!daily.isMissingNode()) {
                        record.setTemperature(parseDouble(daily, "tempMax"));
                        record.setHumidity(parseDouble(daily, "humidity"));
                    }
                    JsonNode hourly = weatherRoot.path("weatherHourly");
                    if (hourly.isArray() && hourly.size() > 0) {
                        JsonNode lastH = hourly.get(hourly.size() - 1);
                        record.setWindDirection(lastH.path("windDir").asText(null));
                        record.setWindSpeed(lastH.path("windSpeed").asText(null));
                        record.setWeather(lastH.path("text").asText(null));
                    }
                }
            } catch (Exception e) {
                log.debug("{} {} 历史天气无数据", city.getName(), date);
            }

            AirQualityData exist = airQualityDataMapper.selectByCityAndDate(city.getId(), date);
            if (exist != null) {
                record.setId(exist.getId());
                airQualityDataMapper.update(record);
            } else {
                airQualityDataMapper.insert(record);
            }
            log.info("{} {} 历史回填成功 AQI={}", city.getName(), date, record.getAqi());
            return true;
        } catch (Exception e) {
            log.warn("{} {} 历史回填失败: {}", city.getName(), date, e.getMessage());
            return false;
        }
    }

    private Integer parseInt(JsonNode parent, String field) {
        JsonNode node = parent.get(field);
        if (node == null || node.isNull()) return null;
        try {
            return Integer.parseInt(node.asText());
        } catch (Exception e) {
            return null;
        }
    }
}
