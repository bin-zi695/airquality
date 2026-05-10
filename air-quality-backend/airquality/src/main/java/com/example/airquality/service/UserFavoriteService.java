package com.example.airquality.service;

import com.example.airquality.entity.UserFavorite;
import com.example.airquality.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;

    public UserFavoriteService(UserFavoriteMapper userFavoriteMapper) {
        this.userFavoriteMapper = userFavoriteMapper;
    }

    public List<UserFavorite> getByUserId(Long userId) {
        return userFavoriteMapper.selectByUserId(userId);
    }

    public List<Long> getCityIdsByUserId(Long userId) {
        return userFavoriteMapper.selectCityIdsByUserId(userId);
    }

    public boolean isFavorite(Long userId, Long cityId) {
        return userFavoriteMapper.selectByUserAndCity(userId, cityId) != null;
    }

    public int countByUserId(Long userId) {
        return userFavoriteMapper.countByUserId(userId);
    }

    public void addFavorite(Long userId, Long cityId) {
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setCityId(cityId);
        userFavoriteMapper.insert(favorite);
    }

    public void removeFavorite(Long userId, Long cityId) {
        userFavoriteMapper.delete(userId, cityId);
    }
}
