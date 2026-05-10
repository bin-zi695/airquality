package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.entity.City;
import com.example.airquality.service.CityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public Result<List<City>> list(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String province,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) Integer status) {
        return Result.success(cityService.list(name, province, category, status));
    }

    @GetMapping("/all")
    public Result<List<City>> listAll() {
        return Result.success(cityService.listAll());
    }

    @GetMapping("/{id}")
    public Result<City> getById(@PathVariable Long id) {
        return Result.success(cityService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody City city) {
        cityService.save(city);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody City city) {
        city.setId(id);
        cityService.update(city);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        cityService.deleteById(id);
        return Result.success();
    }
}
