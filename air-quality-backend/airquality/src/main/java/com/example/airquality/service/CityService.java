package com.example.airquality.service;

import com.example.airquality.common.SqlUtils;
import com.example.airquality.entity.City;
import com.example.airquality.mapper.CityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private final CityMapper cityMapper;
    private final SqlUtils sqlUtils;

    public CityService(CityMapper cityMapper, SqlUtils sqlUtils) {
        this.cityMapper = cityMapper;
        this.sqlUtils = sqlUtils;
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
        sqlUtils.resetCityAutoIncrement(cityMapper.selectMaxId() + 1);
    }
}
