package com.example.airquality.controller;

import java.util.List;

import com.example.airquality.service.OperationLogService;
import com.example.airquality.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
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
    private final OperationLogService operationLogService;
    private final UserService userService;
    private final HttpServletRequest request;

    public AirQualityArticleController(AirQualityArticleService articleService,
                                       OperationLogService operationLogService,
                                       UserService userService,
                                       HttpServletRequest request) {
        this.articleService = articleService;
        this.operationLogService = operationLogService;
        this.userService = userService;
        this.request = request;
    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    private void logAdminOp(Authentication auth, String action, String target, String detail) {
        Long userId = auth != null ? (Long) auth.getPrincipal() : 0L;
        String username = "";
        try { username = userService.getById(userId).getUsername(); } catch (Exception ignored) {}
        operationLogService.log(userId, username, action, target, detail, getClientIp());
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
    public Result<Void> save(@RequestBody AirQualityArticle article, Authentication auth) {
        articleService.save(article);
        logAdminOp(auth, "create", "资讯管理", "新增资讯: " + article.getTitle());
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AirQualityArticle article, Authentication auth) {
        article.setId(id);
        articleService.update(article);
        logAdminOp(auth, "update", "资讯管理", "更新资讯: ID=" + id + " " + article.getTitle());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, Authentication auth) {
        articleService.deleteById(id);
        logAdminOp(auth, "delete", "资讯管理", "删除资讯: ID=" + id);
        return Result.success();
    }
}
