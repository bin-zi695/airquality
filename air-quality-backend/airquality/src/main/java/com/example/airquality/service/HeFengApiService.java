package com.example.airquality.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class HeFengApiService {

    @Value("${hefeng.api.key}")
    private String apiKey;

    private final OkHttpClient client;
    private final ObjectMapper mapper;

    private static final String BASE_URL = "https://devapi.qweather.com/v7/air";

    public HeFengApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        this.mapper = new ObjectMapper();
    }

    public JsonNode getNowAirQuality(String locationId) throws IOException {
        String url = BASE_URL + "/now?location=" + locationId + "&key=" + apiKey;
        return doGet(url);
    }

    public JsonNode getHistoricalAirQuality(String locationId, String date) throws IOException {
        String url = BASE_URL + "/" + date + "?location=" + locationId + "&key=" + apiKey;
        return doGet(url);
    }

    private JsonNode doGet(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }
            return mapper.readTree(response.body().string());
        }
    }
}
