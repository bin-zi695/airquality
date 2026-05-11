package com.example.airquality.controller;

import com.example.airquality.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @PostMapping("/avatar")
    public Result<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "文件为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String newName = UUID.randomUUID().toString() + ext;

        String userDir = System.getProperty("user.dir");
        Path uploadDir = Paths.get(userDir, "uploads", "avatars");
        try {
            Files.createDirectories(uploadDir);
            Path targetPath = uploadDir.resolve(newName);
            file.transferTo(targetPath.toFile());
            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/avatars/" + newName);
            return Result.success(result);
        } catch (IOException e) {
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
}
