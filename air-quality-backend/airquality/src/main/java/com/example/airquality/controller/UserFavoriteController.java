package com.example.airquality.controller;

import com.example.airquality.common.Result;
import com.example.airquality.service.UserFavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
    }

    @GetMapping("/cities")
    public Result<List<Long>> getCityIds(@RequestParam Long userId) {
        return Result.success(userFavoriteService.getCityIdsByUserId(userId));
    }

    @GetMapping("/count")
    public Result<Integer> count(@RequestParam Long userId) {
        return Result.success(userFavoriteService.countByUserId(userId));
    }

    @GetMapping("/check")
    public Result<Boolean> isFavorite(@RequestParam Long userId, @RequestParam Long cityId) {
        return Result.success(userFavoriteService.isFavorite(userId, cityId));
    }

    @PostMapping
    public Result<Void> addFavorite(@RequestParam Long userId, @RequestParam Long cityId) {
        userFavoriteService.addFavorite(userId, cityId);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long cityId) {
        userFavoriteService.removeFavorite(userId, cityId);
        return Result.success();
    }
}
