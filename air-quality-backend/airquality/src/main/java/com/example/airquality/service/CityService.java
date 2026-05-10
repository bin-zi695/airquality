package com.example.airquality.service;

import com.example.airquality.entity.City;
import com.example.airquality.mapper.CityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private final CityMapper cityMapper;

    public CityService(CityMapper cityMapper) {
        this.cityMapper = cityMapper;
    }

    public City getById(Long id) {
        return cityMapper.selectById(id);
    }

    public City getByName(String name) {
        return cityMapper.selectByName(name);
    }

    public List<City> listAll() {
        return cityMapper.selectAll();
    }

    public List<City> list(String name, String province, String category, Integer status) {
        return cityMapper.selectList(name, province, category, status);
    }

    public void save(City city) {
        cityMapper.insert(city);
    }

    public void update(City city) {
        cityMapper.update(city);
    }

    public void deleteById(Long id) {
        cityMapper.deleteById(id);
    }
}
