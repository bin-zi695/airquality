# 🌿 城市空气质量数据管理系统

## 项目简介

基于 **Spring Boot + Vue 3** 前后端分离架构的城市空气质量数据管理系统。普通用户可查询、收藏城市空气质量数据，查看可视化图表；管理员可管理数据、用户、城市、预警配置与科普资讯。数据通过**和风天气 API** 定时采集自动入库，保留最近 15 天数据。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5.14 (JDK 21) |
| 数据库 | MySQL |
| ORM | MyBatis 3.0.5 |
| 缓存 | Redis |
| 安全认证 | Spring Security + JWT |
| AOP | Spring AOP（预警检测切面） |
| IoC 策略 | 策略模式实现 AQI 等级可插拔判定 |
| DDL 执行 | JdbcTemplate（AUTO_INCREMENT 重置） |
| 前端框架 | Vue 3 + Vite |
| UI 库 | Element Plus（含图标） |
| 状态管理 | Pinia |
| 路由 | Vue Router（含角色路由守卫） |
| 图表 | ECharts 5 |
| 数据源 | 和风天气 API（空气质量/天气/历史） |
| HTTP 客户端 | OkHttp |
| JSON 解析 | Jackson |

## 项目结构

```
air-quality-system/
│
├── database/                              # 📦 数据库脚本
│   └── init.sql                           #   建库建表 + 初始化16个城市 + 默认预警阈值
│
├── air-quality-backend/                   # ☕ 后端 Spring Boot 项目 (JDK 21)
│   └── airquality/
│       ├── pom.xml                        #   Maven 依赖配置
│       └── src/main/
│           ├── resources/
│           │   └── application.properties #   数据库/Redis/MyBatis/和风天气连接配置
│           └── java/com/example/airquality/
│               ├── AirqualityApplication.java  # 启动类 (@EnableScheduling)
│               │
│               ├── entity/                     # 📦 实体类（7个）
│               │   ├── User.java               #   用户实体
│               │   ├── City.java               #   城市实体（含经纬度/locationId）
│               │   ├── AirQualityData.java     #   空气质量数据实体（含天气字段）
│               │   ├── UserFavorite.java       #   用户收藏关联实体
│               │   ├── AlertThreshold.java     #   预警阈值实体
│               │   ├── SystemConfig.java       #   系统配置实体
│               │   └── AirQualityArticle.java  #   科普资讯实体
│               │
│               ├── mapper/                     # 📦 MyBatis Mapper 接口（7个，纯注解版）
│               │   ├── UserMapper.java         #   用户数据操作 + selectMaxId
│               │   ├── CityMapper.java         #   城市增删改查 + selectMaxId
│               │   ├── AirQualityDataMapper.java # 空气质量数据 CRUD + 批量 + 趋势 + 清理 + selectMaxId
│               │   ├── UserFavoriteMapper.java #   收藏管理
│               │   ├── AlertThresholdMapper.java  # 阈值管理 + selectMaxId
│               │   ├── SystemConfigMapper.java #   系统配置读写
│               │   └── AirQualityArticleMapper.java # 资讯管理 + selectMaxId
│               │
│               ├── service/                    # 📦 业务服务层
│               │   ├── UserService.java        #   用户注册/登录/修改/删除（含ID重置）
│               │   ├── CityService.java        #   城市管理（含ID重置）
│               │   ├── AirQualityDataService.java # 空气质量数据查询/趋势/删除（含ID重置）
│               │   ├── UserFavoriteService.java   # 收藏管理
│               │   ├── AlertThresholdService.java # 预警阈值管理（含ID重置）
│               │   ├── SystemConfigService.java   # 系统配置
│               │   ├── AirQualityArticleService.java # 资讯管理（含ID重置）
│               │   ├── AqiLevelService.java    # ★ AQI 等级判定（IoC 注入 6 种策略）
│               │   ├── DataSyncService.java    # ★ 数据同步（实时采集 + 历史回填 + 15天清理）
│               │   ├── HeFengApiService.java   # ★ 和风天气 API 调用（空气/天气/历史）
│               │   ├── SyncScheduler.java      # ★ 定时任务调度（4次/天）
│               │   └── strategy/               # ★ Spring IoC 策略模式——AQI 等级判定
│               │       ├── AqiLevelStrategy.java
│               │       ├── ExcellentLevelStrategy.java  # 优 (0-50)
│               │       ├── GoodLevelStrategy.java       # 良 (51-100)
│               │       ├── MildPollutionStrategy.java   # 轻度污染 (101-150)
│               │       ├── ModeratePollutionStrategy.java # 中度污染 (151-200)
│               │       ├── HeavyPollutionStrategy.java  # 重度污染 (201-300)
│               │       └── SeverePollutionStrategy.java # 严重污染 (>300)
│               │
│               ├── controller/                 # 📦 REST API 控制器（9个）
│               │   ├── AuthController.java     #   登录/注册（JWT 签发）
│               │   ├── UserController.java     #   用户管理（管理员）
│               │   ├── CityController.java     #   城市管理（管理员）
│               │   ├── AirQualityDataController.java # 空气质量数据查询/详情/趋势/AQI等级
│               │   ├── UserFavoriteController.java   # 收藏管理
│               │   ├── AlertThresholdController.java  # 预警阈值管理（管理员）
│               │   ├── SyncController.java     # ★ 数据采集（手动触发 + 历史回填）
│               │   ├── SystemConfigController.java    # 系统配置
│               │   ├── UploadController.java          # 文件上传
│               │   └── AirQualityArticleController.java # 科普资讯管理
│               │
│               ├── aop/                         # ★ Spring AOP 切面
│               │   └── AlertDetectionAspect.java #   拦截数据入库自动检测 AQI 预警
│               │
│               ├── security/                    # 🔐 安全认证
│               │   ├── JwtUtil.java             #   JWT 生成/解析/验证
│               │   ├── JwtAuthenticationFilter.java # 请求拦截解析 Token
│               │   └── SecurityConfig.java      #   Spring Security 配置（CORS/权限/放行）
│               │
│               └── common/                      # 📦 公共类
│                   ├── Result.java              #   统一 API 响应格式 {code, message, data}
│                   ├── AqiLevelResult.java      #   AQI 等级判定结果（等级名/颜色/健康提示）
│                   └── SqlUtils.java            #   AUTO_INCREMENT 重置工具（JdbcTemplate）
│
├── air-quality-frontend/                        # 🖥 Vue 3 前端项目
│   ├── package.json                             #   依赖配置
│   ├── vite.config.js                           #   Vite + 代理 + @别名
│   ├── index.html                               #   入口 HTML
│   └── src/
│       ├── main.js                              #   入口（加载 ElementPlus/Icons/路由/Pinia）
│       ├── App.vue                              #   根组件
│       │
│       ├── styles/
│       │   └── global.css                       #   全局样式
│       │
│       ├── api/                                 # 📡 接口层
│       │   ├── request.js                       #   Axios 封装（Token 注入/错误拦截）
│       │   └── index.js                         #   全部 API 模块导出
│       │
│       ├── store/
│       │   └── user.js                          #   Pinia 用户状态（登录/登出/角色判断）
│       │
│       ├── router/
│       │   └── index.js                         #   Vue Router（路由守卫+角色拦截）
│       │
│       └── views/                               # 📄 页面组件（16个）
│           ├── Login.vue                        #   登录页
│           ├── Register.vue                     #   注册页
│           ├── layout/Layout.vue                #   后台布局（顶栏+侧栏+内容区）
│           ├── dashboard/Dashboard.vue          #   首页（统计卡片+收藏城市AQI卡片+入口）
│           ├── air-quality/AirQualityList.vue   #   空气质量查询列表（15天内筛选）
│           ├── air-quality/AirQualityDetail.vue #   单条数据详情
│           ├── charts/Charts.vue                #   数据可视化（趋势图+饼图+对比图，默认7天）
│           ├── favorites/Favorites.vue          #   我的收藏（添加/删除城市）
│           ├── articles/Articles.vue            #   科普资讯列表
│           ├── articles/ArticleDetail.vue       #   资讯详情页
│           ├── profile/Profile.vue              #   个人中心
│           └── admin/                           # 👑 管理员页面
│               ├── Users.vue                    #   用户管理
│               ├── Cities.vue                   #   城市管理
│               ├── AirData.vue                  #   数据管理（采集按钮+增删改查）
│               ├── AlertThreshold.vue           #   预警阈值配置
│               └── Articles.vue                 #   资讯发布管理
```

## 数据库表

| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `city` | 城市表（云南省16个城市，含经纬度+locationId） |
| `air_quality_data` | 空气质量数据表（AQI/PM2.5/PM10/SO₂/NO₂/CO/O₃ + 温度/湿度/风向/风速/天气） |
| `user_favorite` | 用户收藏关联表 |
| `alert_threshold` | 预警阈值配置表 |
| `system_config` | 系统配置表 |
| `air_quality_article` | 科普资讯表 |

## API 接口一览

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/login` | 用户登录（返回 JWT） | 公开 |
| GET | `/api/cities/all` | 获取所有城市 | 公开 |
| GET | `/api/air-data` | 查询空气质量数据 | 公开 |
| GET | `/api/air-data/{id}` | 获取单条数据详情 | 公开 |
| GET | `/api/air-data/latest` | 获取城市最新数据 | 公开 |
| GET | `/api/air-data/favorites-latest` | 获取收藏城市最新数据 | 公开 |
| GET | `/api/air-data/favorites-by-date` | 按日期获取收藏数据 | 公开 |
| GET | `/api/air-data/favorites-dates` | 获取收藏数据可查日期 | 公开 |
| GET | `/api/air-data/trend` | 获取趋势数据 | 公开 |
| GET | `/api/air-data/all-dates` | 获取所有有数据日期 | 公开 |
| GET | `/api/air-data/all-by-date` | 按日期获取所有城市数据 | 公开 |
| GET | `/api/air-data/aqi-level/{aqi}` | 获取 AQI 等级判定结果 | 公开 |
| GET | `/api/articles/published` | 获取已发布资讯 | 公开 |
| ★ POST | `/api/admin/sync-now` | 手动触发数据采集 | 公开 |
| ★ GET | `/api/admin/sync-history?days=N` | 回填最近 N 天历史数据 | 公开 |
| GET/POST/PUT/DELETE | `/api/users/**` | 用户管理 | 管理员 |
| GET/POST/PUT/DELETE | `/api/cities/**` | 城市管理（除 all） | 管理员 |
| GET/POST/PUT/DELETE | `/api/alert-thresholds/**` | 预警阈值管理 | 管理员 |
| GET/POST/PUT/DELETE | `/api/articles/**` | 资讯管理（除 published） | 管理员 |

## 数据采集方案

### 数据源

通过**和风天气 API** 获取实时数据，使用的 API 包括：

| API | 路径 | 说明 |
|------|------|------|
| 空气质量实况 | `/airquality/v1/current/{lat}/{lng}` | 新版空气质量 API（经纬度坐标） |
| 实时天气 | `/v7/weather/now` | 温度/湿度/风向/风速/天气状况 |
| 历史空气质量 | `/v7/historical/air` | 时光机 API（locationId + date） |
| 历史天气 | `/v7/historical/weather` | 时光机天气数据 |

### 定时任务

每天 4 次自动采集，数据保留最近 15 天：

| 时间 | API | 采集对象 | 说明 |
|------|-----|----------|------|
| 凌晨 1:00 | 时光机 API | 昨天 | 回填昨日完整数据 |
| 早上 8:00 | 实时 API | 今天 | 早间空气质量数据 |
| 下午 14:00 | 实时 API | 今天 | 午间更新 |
| 晚上 20:00 | 实时 API | 今天 | 晚间更新 |

### 数据保留策略

每次采集完成后自动删除 15 天前的数据，确保数据库只保留最近 15 天记录。

## 特色功能

### AUTO_INCREMENT 自动重置

删除任意记录后，下次新增时 ID 会自动回到被删除的位置，不会跳号。

- 新增 [SqlUtils.java](file:///e:/air-quality-system/air-quality-backend/airquality/src/main/java/com/example/airquality/common/SqlUtils.java) — 使用 JdbcTemplate 执行 DDL
- 每个含删除操作的 Service 在 delete 后自动调用 `resetAutoIncrement`
- 覆盖表：`user`、`city`、`air_quality_data`、`air_quality_article`、`alert_threshold`

### 前端日期筛选限制

所有日期选择组件限制只能查询最近 15 天数据，与数据库保留策略一致。

### 日志精简

控制台日志级别已优化，仅显示 WARN 级别及以上系统日志，保留项目业务 INFO 日志。

## 启动指南

### 前置要求

- JDK 21（必须，JDK 17 有 WeakPairMap Bug）
- MySQL 8.0+
- Redis
- Node.js 18+
- 和风天气开发者账号（配置 API Key）

### 1. 数据库初始化

```bash
mysql -u root -p < database/init.sql
```

### 2. 配置和风天气 API

编辑 `air-quality-backend/airquality/src/main/resources/application.properties`：

```properties
hefeng.api.host=https://your_api_host
hefeng.api.key=your_api_key
```

### 3. 启动后端

在 IDEA 中确保：
- **文件 → 项目结构 → SDK**：选 JDK 21
- **运行 → 编辑配置 → JRE**：选 JDK 21

```bash
cd air-quality-backend/airquality
./mvnw spring-boot:run        # 端口 8080（需要 JDK 21）
```

### 4. 启动前端

```bash
cd air-quality-frontend
npm install
npm run dev                   # 端口 3000（已代理 /api 到 8080）
```

### 5. 设置管理员

注册后用 MySQL 将用户角色改为 admin：

```sql
UPDATE air_quality_db.user SET role = 'admin' WHERE username = '你的用户名';
```

### 6. 初始化数据

启动后访问 `http://localhost:8080/api/admin/sync-history?days=4` 回填最近 4 天历史数据。
