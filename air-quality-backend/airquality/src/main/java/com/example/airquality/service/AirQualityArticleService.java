package com.example.airquality.service;

import com.example.airquality.common.SqlUtils;
import com.example.airquality.entity.AirQualityArticle;
import com.example.airquality.mapper.AirQualityArticleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirQualityArticleService {

    private final AirQualityArticleMapper articleMapper;
    private final SqlUtils sqlUtils;

    public AirQualityArticleService(AirQualityArticleMapper articleMapper, SqlUtils sqlUtils) {
        this.articleMapper = articleMapper;
        this.sqlUtils = sqlUtils;
    }

    public AirQualityArticle getById(Long id) {
        return articleMapper.selectById(id);
    }

    public List<AirQualityArticle> listPublished() {
        return articleMapper.selectPublished();
    }

    public List<AirQualityArticle> list(String title, Integer status) {
        return articleMapper.selectList(title, status);
    }

    public void save(AirQualityArticle article) {
        articleMapper.insert(article);
    }

    public void update(AirQualityArticle article) {
        articleMapper.update(article);
    }

    public void deleteById(Long id) {
        articleMapper.deleteById(id);
        sqlUtils.resetArticleAutoIncrement(articleMapper.selectMaxId() + 1);
    }
}
