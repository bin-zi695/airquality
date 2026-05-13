package com.example.airquality.config;

import com.example.airquality.entity.User;
import com.example.airquality.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitConfig {

    @Bean
    public CommandLineRunner initAdmin(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@air.com";
            if (userService.getByEmail(adminEmail) == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNickname("系统管理员");
                admin.setRole("admin");
                admin.setStatus(1);
                userService.register(admin);
                System.out.println("=== 管理员账号已初始化 ===");
                System.out.println("用户名: admin");
                System.out.println("邮箱: admin@air.com");
                System.out.println("密码: admin123");
                System.out.println("=== ================= ===");
            }
        };
    }
}
