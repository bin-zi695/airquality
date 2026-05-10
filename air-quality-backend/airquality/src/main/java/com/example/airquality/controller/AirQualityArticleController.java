package com.example.airquality.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.airquality.common.Result;
import com.example.airquality.entity.AirQualityArticle;
import com.example.airquality.service.AirQualityArticleService;

@RestController
@RequestMapping("/api/articles")
public class AirQualityArticleController {

    private final AirQualityArticleService articleService;

    public AirQualityArticleController(AirQualityArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/published")
    public Result<List<AirQualityArticle>> listPublished() {
        return Result.success(articleService.listPublished());
    }

    @GetMapping
    public Result<List<AirQualityArticle>> list(@RequestParam(required = false) String title,
                                                 @RequestParam(required = false) Integer status) {
        return Result.success(articleService.list(title, status));
    }

    @GetMapping("/{id}")
    public Result<AirQualityArticle> getById(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody AirQualityArticle article) {
        articleService.save(article);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AirQualityArticle article) {
        article.setId(id);
        articleService.update(article);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteById(id);
        return Result.success();
    }
}
