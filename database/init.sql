CREATE DATABASE IF NOT EXISTS air_quality_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE air_quality_db;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50),
    `email` VARCHAR(100) UNIQUE,
    `phone` VARCHAR(20),
    `avatar` VARCHAR(255),
    `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT 'user / admin',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 城市表
DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL,
    `province` VARCHAR(50),
    `latitude` DOUBLE,
    `longitude` DOUBLE,
    `category` VARCHAR(50) COMMENT '城市分类',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市表';

-- 空气质量数据表
DROP TABLE IF EXISTS `air_quality_data`;
CREATE TABLE `air_quality_data` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `city_id` BIGINT NOT NULL,
    `date` DATE NOT NULL,
    `aqi` INT,
    `pm25` DOUBLE,
    `pm10` DOUBLE,
    `so2` DOUBLE,
    `no2` DOUBLE,
    `co` DOUBLE,
    `o3` DOUBLE,
    `temperature` DOUBLE COMMENT '温度(℃)',
    `humidity` DOUBLE COMMENT '湿度(%)',
    `wind_direction` VARCHAR(20) COMMENT '风向',
    `wind_speed` VARCHAR(20) COMMENT '风力',
    `weather` VARCHAR(50) COMMENT '天气状况',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_city_date` (`city_id`, `date`),
    INDEX `idx_date` (`date`),
    INDEX `idx_aqi` (`aqi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='空气质量数据表';

-- 用户收藏城市表
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `city_id` BIGINT NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_city` (`user_id`, `city_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏城市表';

-- 预警阈值配置表
DROP TABLE IF EXISTS `alert_threshold`;
CREATE TABLE `alert_threshold` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `threshold_name` VARCHAR(50) NOT NULL COMMENT '阈值名称',
    `aqi_threshold` INT DEFAULT 100 COMMENT 'AQI阈值',
    `pm25_threshold` DOUBLE DEFAULT 75 COMMENT 'PM2.5阈值',
    `pm10_threshold` DOUBLE DEFAULT 150 COMMENT 'PM10阈值',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警阈值配置表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT,
    `username` VARCHAR(50),
    `operation_type` VARCHAR(50) NOT NULL COMMENT 'INSERT/UPDATE/DELETE/LOGIN',
    `module` VARCHAR(50) COMMENT '操作模块',
    `description` VARCHAR(500) COMMENT '操作描述',
    `request_method` VARCHAR(10),
    `request_url` VARCHAR(255),
    `request_params` TEXT,
    `ip_address` VARCHAR(50),
    `status` TINYINT DEFAULT 1 COMMENT '1成功 0失败',
    `error_msg` VARCHAR(500),
    `execution_time` BIGINT COMMENT '执行耗时(ms)',
    `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_operation_time` (`operation_time`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 访问日志表
DROP TABLE IF EXISTS `access_log`;
CREATE TABLE `access_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT,
    `username` VARCHAR(50),
    `ip_address` VARCHAR(50),
    `request_method` VARCHAR(10),
    `request_url` VARCHAR(255),
    `request_params` TEXT,
    `response_status` INT,
    `execution_time` BIGINT COMMENT '请求耗时(ms)',
    `user_agent` VARCHAR(500),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_request_url` (`request_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志表';

-- 系统配置表
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `config_key` VARCHAR(100) NOT NULL UNIQUE,
    `config_value` TEXT,
    `description` VARCHAR(255),
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 空气质量科普资讯表
DROP TABLE IF EXISTS `air_quality_article`;
CREATE TABLE `air_quality_article` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `content` LONGTEXT,
    `summary` VARCHAR(500),
    `cover_image` VARCHAR(255),
    `author` VARCHAR(50),
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1发布 0草稿',
    `publish_time` DATETIME,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='空气质量科普资讯表';

-- 管理员账号由应用启动时自动初始化 (用户名: admin, 邮箱: admin@air.com, 密码: admin123)

-- 插入默认预警阈值
INSERT INTO `alert_threshold` (`threshold_name`, `aqi_threshold`, `pm25_threshold`, `pm10_threshold`, `enabled`) VALUES
('默认预警阈值', 150, 75, 150, 1);

-- 插入默认城市数据（云南省16个城市）
INSERT INTO `city` (`name`, `province`, `latitude`, `longitude`, `category`) VALUES
('昆明市', '云南', 25.0389, 102.7183, '省会城市'),
('曲靖市', '云南', 25.4900, 103.8000, '地级市'),
('玉溪市', '云南', 24.3500, 102.5500, '地级市'),
('保山市', '云南', 25.1200, 99.1700, '地级市'),
('昭通市', '云南', 27.3400, 103.7200, '地级市'),
('丽江市', '云南', 26.8721, 100.2299, '地级市'),
('普洱市', '云南', 22.7800, 100.9700, '地级市'),
('临沧市', '云南', 23.8800, 100.0800, '地级市'),
('楚雄市', '云南', 25.0400, 101.5500, '县级市'),
('蒙自市', '云南', 23.3700, 103.4000, '县级市'),
('文山市', '云南', 23.3700, 104.2400, '县级市'),
('景洪市', '云南', 22.0000, 100.8000, '县级市'),
('大理市', '云南', 25.6065, 100.2676, '县级市'),
('芒市', '云南', 24.4300, 98.5800, '县级市'),
('泸水市', '云南', 25.8200, 98.8500, '县级市'),
('香格里拉市', '云南', 27.8300, 99.7000, '县级市');

-- 科普资讯
INSERT INTO `air_quality_article` (`title`, `content`, `summary`, `author`, `status`, `publish_time`) VALUES
('什么是AQI？一文读懂空气质量指数',
'# 什么是AQI？

AQI（Air Quality Index）即空气质量指数，是定量描述空气质量状况的无量纲指数。

## AQI 的计算方法

AQI 是根据各项污染物的浓度计算得出的。参与评价的污染物包括：
- **细颗粒物（PM2.5）**
- **可吸入颗粒物（PM10）**
- **二氧化硫（SO₂）**
- **二氧化氮（NO₂）**
- **臭氧（O₃）**
- **一氧化碳（CO）**

每种污染物都有一个对应的空气质量分指数（IAQI），最终的 AQI 取所有分指数中的**最大值**。

## AQI 等级划分

| AQI 范围 | 等级 | 颜色 | 健康影响 |
|----------|------|------|----------|
| 0-50 | 优 | 绿色 | 基本无空气污染 |
| 51-100 | 良 | 黄色 | 极少数敏感人群应减少户外活动 |
| 101-150 | 轻度污染 | 橙色 | 敏感人群症状有轻度加剧 |
| 151-200 | 中度污染 | 红色 | 对健康人群心脏、呼吸系统有影响 |
| 201-300 | 重度污染 | 紫色 | 健康人群普遍出现症状 |
| >300 | 严重污染 | 褐红色 | 健康人群运动耐受力降低 |

AQI 数值越大，说明空气污染越严重，对人体健康的影响也越大。',
'全面解析AQI空气质量指数的定义、计算方法和等级划分标准，帮助您快速理解空气质量数据。',
'系统管理员', 1, NOW()),

('PM2.5 的危害与防护指南',
'# PM2.5 的危害与防护指南

## 什么是 PM2.5？

PM2.5 是指环境空气中**空气动力学当量直径小于等于 2.5 微米**的颗粒物。它能较长时间悬浮于空气中，因其粒径小、活性强，易附带有毒有害物质，对人体健康和环境质量影响很大。

## PM2.5 的来源

- **燃煤、燃油**等化石燃料燃烧
- **机动车尾气**排放
- **建筑扬尘**和道路扬尘
- **工业排放**和二次转化

## 健康危害

PM2.5 由于粒径小，可以穿透人体呼吸系统的防御屏障，直接进入肺泡甚至进入血液循环系统，引发：
- 呼吸系统疾病（哮喘、支气管炎）
- 心血管疾病
- 免疫系统损伤
- 长期暴露可能致癌

## 防护建议

### 当 PM2.5 超标时：

1. **减少户外活动**：尽量避免长时间在户外逗留
2. **佩戴防护口罩**：选择 N95 或 KN95 级别的口罩
3. **关闭门窗**：开启空气净化器
4. **多饮水**：帮助身体排出吸入的有害物质
5. **清淡饮食**：多食富含维生素的水果蔬菜

### 日常预防：

- 关注每日空气质量预报
- 雾霾天开车的朋友注意减速慢行
- 室内种植绿植如绿萝、吊兰等净化空气',
'详解PM2.5颗粒物的来源、健康危害及科学防护措施。',
'系统管理员', 1, NOW()),

('春季空气质量变化与过敏防护',
'# 春季空气质量变化与过敏防护

春天是万物复苏的季节，但也是空气质量和过敏问题高发的时期。

## 春季空气污染特点

春季空气污染呈现以下几个特点：

### 1. 沙尘天气多发
北方地区春季常受沙尘暴影响，PM10 浓度急剧升高，对呼吸系统造成较大刺激。

### 2. 花粉浓度升高
随着植物开花，空气中花粉浓度大幅上升，容易引发过敏性鼻炎、哮喘等疾病。

### 3. 臭氧污染抬头
随着日照增强和气温升高，臭氧（O₃）浓度开始上升，成为春夏季节的主要污染物之一。

## 春季防护建议

1. **关注花粉指数**：花粉过敏者在花粉浓度高时减少外出
2. **佩戴口罩**：可有效阻隔花粉和颗粒物
3. **及时洗漱**：回家后洗脸、漱口、清洗鼻腔
4. **室内通风时机**：选择清晨或雨后开窗通风
5. **饮食调理**：多摄入富含维生素C的食物，增强抵抗力

## 特别提醒

春天气温变化大，空气质量波动也较频繁。建议您通过本系统收藏关注城市，随时了解最新的空气质量状况，合理安排出行计划。',
'春季花粉、沙尘和臭氧污染的特点解析，以及过敏人群的防护指南。',
'系统管理员', 1, NOW()),

('如何看懂空气质量数据？6项指标详解',
'# 如何看懂空气质量数据？

空气质量数据包含多项指标，理解每项指标的含义是科学防护的基础。

## 六项核心指标

### 1. AQI（空气质量指数）
综合评价空气质量的指标，取值范围 0-500，数值越大污染越重。

### 2. PM2.5（细颗粒物）
粒径 ≤2.5 微米的颗粒物，能进入肺泡和血液，是危害最大的污染物之一。**WHO 指导值：年均 5 μg/m³。**

### 3. PM10（可吸入颗粒物）
粒径 ≤10 微米的颗粒物，主要沉积在上呼吸道。**WHO 指导值：年均 15 μg/m³。**

### 4. SO₂（二氧化硫）
主要来源于含硫燃料燃烧，对呼吸系统有刺激作用，可形成酸雨。

### 5. NO₂（二氧化氮）
主要来源于机动车尾气和工业排放，对肺部有较强的刺激和腐蚀作用。

### 6. O₃（臭氧）
近地面臭氧是光化学烟雾的主要成分，对眼睛和呼吸道有强烈刺激性。夏季午后浓度最高。

### 7. CO（一氧化碳）
不完全燃烧产物，与血红蛋白结合能力强，降低血液携氧能力。

## 快速判断技巧

- **AQI ≤ 50** → 空气优良，适合户外活动
- **AQI 51-100** → 空气质量可接受
- **AQI 101-150** → 敏感人群应减少户外活动
- **AQI > 150** → 所有人应减少户外活动

通过本系统的数据可视化功能，您可以直观地看到各项指标的变化趋势。',
'逐一解读AQI、PM2.5、PM10等六项核心空气质量指标的含义和健康参考值。',
'系统管理员', 1, NOW()),

('臭氧污染的危害与夏季防护',
'# 臭氧污染的危害与夏季防护

## 认识臭氧

很多人以为臭氧层是"好东西"，但它只在高空才有保护作用。**近地面臭氧**是一种刺激性很强的污染物，对人体健康和环境都有较大危害。

## 臭氧的来源

近地面臭氧不是直接排放的，而是由**氮氧化物（NOx）和挥发性有机物（VOCs）**在强烈阳光照射下发生光化学反应生成的。因此：

- **夏季午后**臭氧浓度最高
- **晴天无风**天气臭氧污染最严重
- 城市下风向地区臭氧浓度往往更高

## 健康危害

臭氧具有强烈的氧化性，对健康的损害主要表现为：

1. **刺激呼吸道**：引起咳嗽、咽喉不适
2. **损害肺功能**：降低肺活量，加重哮喘
3. **刺激眼睛**：引起眼睛干涩、流泪
4. **降低免疫力**：增加呼吸道感染风险

## 夏季臭氧防护指南

1. **关注监测数据**：通过本系统查看每日 O₃ 浓度
2. **错峰出行**：午后12:00-16:00 臭氧高发时段减少户外运动
3. **室内防护**：高温时段关闭门窗，减少通风换气
4. **特殊人群**：儿童、老人及呼吸系统疾病患者尤其要注意防护

臭氧污染看不见摸不着，但危害不可忽视。科学防护从了解数据开始！',
'认识近地面臭氧的形成机制、健康危害，掌握夏季臭氧高发期的科学防护方法。',
'系统管理员', 1, NOW()),

('冬季雾霾成因与科学应对策略',
'# 冬季雾霾成因与科学应对策略

## 什么是雾霾？

雾霾是雾和霾的统称。**雾**是大量悬浮在近地面空气中的微小水滴形成的气溶胶系统；**霾**是由空气中灰尘、硫酸、硝酸等颗粒物组成的气溶胶系统，核心物质是 PM2.5。

## 冬季为什么雾霾严重？

### 1. 气象因素
- **逆温层**：冬季近地面气温低于高空，形成"逆温层"，像锅盖一样锁住污染物
- **静稳天气**：冬季风速小，空气流动性差，污染物不易扩散
- **湿度较高**：水汽与颗粒物结合，加重霾的形成

### 2. 人为因素
- **供暖燃煤**：北方冬季供暖导致燃煤量大幅增加
- **机动车排放**：低温环境下发动机燃烧不充分，排放增加
- **工业排放**持续存在

## 科学应对策略

### 出行篇
- 关注 AQI 预报，AQI > 150 时减少不必要的出行
- 必须外出时佩戴 KN95/N95 级别口罩
- 尽量选择公共交通工具

### 居家篇
- 关闭门窗，使用**空气净化器**
- 开启加湿器，增加室内湿度有助于颗粒物沉降
- 不在室内吸烟

### 饮食篇
- 多喝水，促进新陈代谢
- 多食用富含维生素的食物（胡萝卜、西兰花、橙子等）
- 适量摄入清肺食物（木耳、百合、梨等）

雾霾治理需要全社会共同努力，从了解数据到科学防护，每个人都可以为自己的健康负责。',
'解析冬季雾霾高发的气象与人为成因，提供出行、居家、饮食全方位的科学防护建议。',
'系统管理员', 1, NOW());
