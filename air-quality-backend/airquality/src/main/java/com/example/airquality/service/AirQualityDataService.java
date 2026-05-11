package com.example.airquality.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.airquality.entity.AirQualityData;
import com.example.airquality.mapper.AirQualityDataMapper;

@Service
public class AirQualityDataService {

    private final AirQualityDataMapper airQualityDataMapper;

    public AirQualityDataService(AirQualityDataMapper airQualityDataMapper) {
        this.airQualityDataMapper = airQualityDataMapper;
    }

    public AirQualityData getById(Long id) {
        return airQualityDataMapper.selectById(id);
    }

    public AirQualityData getByCityAndDate(Long cityId, LocalDate date) {
        return airQualityDataMapper.selectByCityAndDate(cityId, date);
    }

    public List<AirQualityData> list(Long cityId, LocalDate startDate, LocalDate endDate) {
        return airQualityDataMapper.selectList(cityId, startDate, endDate);
    }

    public List<AirQualityData> getLatestByFavorites(Long userId) {
        return airQualityDataMapper.selectLatestByFavorites(userId);
    }

    public List<LocalDate> getFavoriteDates(Long userId) {
        return airQualityDataMapper.selectFavoriteDates(userId);
    }

    public List<AirQualityData> getFavoritesByDate(Long userId, LocalDate date) {
        return airQualityDataMapper.selectFavoritesByDate(userId, date);
    }

    public List<LocalDate> getAllDates() {
        return airQualityDataMapper.selectAllDates();
    }

    public List<AirQualityData> getAllByDate(LocalDate date) {
        return airQualityDataMapper.selectAllByDate(date);
    }

    public AirQualityData getLatestByCity(Long cityId) {
        return airQualityDataMapper.selectLatestByCity(cityId);
    }

    public List<AirQualityData> getTrend(Long cityId, LocalDate startDate, LocalDate endDate) {
        return airQualityDataMapper.selectTrend(cityId, startDate, endDate);
    }

    public List<AirQualityData> getByCity(Long cityId) {
        return airQualityDataMapper.selectByCity(cityId);
    }

    public void save(AirQualityData data) {
        airQualityDataMapper.insert(data);
    }

    public void update(AirQualityData data) {
        airQualityDataMapper.update(data);
    }

    public void deleteById(Long id) {
        airQualityDataMapper.deleteById(id);
    }

    public void deleteByCityId(Long cityId) {
        airQualityDataMapper.deleteByCityId(cityId);
    }

    public void batchSave(List<AirQualityData> list) {
        airQualityDataMapper.batchInsert(list);
    }
}
