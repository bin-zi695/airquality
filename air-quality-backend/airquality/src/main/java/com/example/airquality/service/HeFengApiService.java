package com.example.airquality.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class HeFengApiService {

    private static final Logger log = LoggerFactory.getLogger(HeFengApiService.class);

    @Value("${hefeng.api.key}")
    private String apiKey;

    @Value("${hefeng.api.host}")
    private String apiHost;

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    private String airBaseUrl;
    private String weatherBaseUrl;
    private String historicalBaseUrl;

    public HeFengApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        this.airBaseUrl = apiHost + "/airquality/v1/current";
        this.weatherBaseUrl = apiHost + "/v7/weather/now";
        this.historicalBaseUrl = apiHost + "/v7/historical";
        if (apiKey == null || apiKey.isBlank() || "YOUR_API_KEY_HERE".equals(apiKey)) {
            log.warn("=== 和风天气API Key未配置! 请在application.properties中设置 hefeng.api.key ===");
        } else {
            log.info("和风天气API已配置, Host: {}, Key前缀: {}...", apiHost, apiKey.substring(0, Math.min(6, apiKey.length())));
        }
    }

    public JsonNode getNowAirQuality(double lat, double lng) throws IOException {
        String url = airBaseUrl + "/" + fmt(lat) + "/" + fmt(lng) + "?key=" + apiKey;
        log.info("请求空气质量API: {}", url.replace(apiKey, "***"));
        return doGet(url);
    }

    public JsonNode getNowWeather(double lat, double lng) throws IOException {
        String url = weatherBaseUrl + "?location=" + fmt(lng) + "," + fmt(lat) + "&key=" + apiKey;
        log.info("请求天气预报API: {}", url.replace(apiKey, "***"));
        return doGet(url);
    }

    public JsonNode getHistoricalAir(String locationId, String dateStr) throws IOException {
        String url = historicalBaseUrl + "/air?location=" + locationId + "&date=" + dateStr + "&key=" + apiKey;
        log.info("请求历史空气质量API: {}", url.replace(apiKey, "***"));
        return doGet(url);
    }

    public JsonNode getHistoricalWeather(String locationId, String dateStr) throws IOException {
        String url = historicalBaseUrl + "/weather?location=" + locationId + "&date=" + dateStr + "&key=" + apiKey;
        log.info("请求历史天气API: {}", url.replace(apiKey, "***"));
        return doGet(url);
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }

    private JsonNode doGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "(空)";
                log.warn("API请求失败: HTTP {} url={} 响应: {}", response.code(), url.replace(apiKey, "***"), errorBody);
                return null;
            }
            if (response.body() == null) {
                log.warn("API响应body为空");
                return null;
            }
            String body = response.body().string();
            log.debug("API响应: {}", body.length() > 200 ? body.substring(0, 200) + "..." : body);
            return mapper.readTree(body);
        } catch (Exception e) {
            log.error("API请求异常: {}", e.getMessage(), e);
            throw e;
        }
    }
}
