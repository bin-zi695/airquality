# 🌿 城市空气质量数据管理系统

## 项目简介

基于 **Spring Boot + Vue 3** 前后端分离架构的城市空气质量数据管理系统。普通用户可查询、收藏城市空气质量数据，查看可视化图表；管理员可管理数据、用户、日志、预警配置与科普资讯。数据层通过 Python 爬虫定时采集天气后报平台数据自动入库。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5.14 |
| 数据库 | MySQL |
| ORM | MyBatis 3.0.5 |
| 缓存 | Redis |
| 安全认证 | Spring Security + JWT |
| AOP | Spring AOP（操作日志、访问日志、预警检测） |
| IoC策略 | 策略模式实现 AQI 等级可插拔判定 |
| 前端框架 | Vue 3 + Vite |
| UI库 | Element Plus |
| 状态管理 | Pinia |
| 路由 | Vue Router（含角色路由守卫） |
| 图表 | ECharts 5 |
| 爬虫 | Python + requests + BeautifulSoup |
| 数据库反向生成 | MyBatisX（反向生成实体类、Mapper 接口、Service 层、Controller 层） |

## 项目结构

```
air-quality-system/
│
├── database/                              # 📦 数据库脚本
│   └── init.sql                           #   建库建表 + 初始化10个城市 + 默认预警阈值
│
├── air-quality-backend/                   # ☕ 后端 Spring Boot 项目
│   └── airquality/
│       ├── pom.xml                        #   Maven 依赖配置
│       └── src/main/
│           ├── resources/
│           │   └── application.properties #   数据库/Redis/MyBatis 连接配置
│           └── java/com/example/airquality/
│               ├── AirqualityApplication.java  # 启动类
│               │
│               ├── entity/                     # 📦 实体类（9个，对应9张数据库表）
│               │   ├── User.java               #   用户实体
│               │   ├── City.java               #   城市实体
│               │   ├── AirQualityData.java     #   空气质量数据实体
│               │   ├── UserFavorite.java       #   用户收藏关联实体
│               │   ├── AlertThreshold.java     #   预警阈值实体
│               │   ├── OperationLog.java       #   操作日志实体
│               │   ├── AccessLog.java          #   访问日志实体
│               │   ├── SystemConfig.java       #   系统配置实体
│               │   └── AirQualityArticle.java  #   科普资讯实体
│               │
│               ├── mapper/                     # 📦 MyBatis Mapper 接口（9个，纯注解版）
│               │   ├── UserMapper.java         #   用户数据操作
│               │   ├── CityMapper.java         #   城市增删改查
│               │   ├── AirQualityDataMapper.java # 空气质量数据 CRUD + 批量 + 趋势
│               │   ├── UserFavoriteMapper.java #   收藏管理
│               │   ├── AlertThresholdMapper.java  # 阈值管理
│               │   ├── OperationLogMapper.java #   操作日志入库
│               │   ├── AccessLogMapper.java    #   访问日志入库
│               │   ├── SystemConfigMapper.java #   系统配置读写
│               │   └── AirQualityArticleMapper.java # 资讯管理
│               │
│               ├── service/                    # 📦 业务服务层
│               │   ├── UserService.java        #   用户注册/登录/修改/状态管理
│               │   ├── CityService.java        #   城市管理
│               │   ├── AirQualityDataService.java # 空气质量数据查询/趋势/收藏
│               │   ├── UserFavoriteService.java   # 收藏管理
│               │   ├── AlertThresholdService.java # 预警阈值管理
│               │   ├── OperationLogService.java   # 操作日志存储
│               │   ├── AccessLogService.java      # 访问日志存储
│               │   ├── SystemConfigService.java   # 系统配置
│               │   ├── AirQualityArticleService.java # 资讯管理
│               │   ├── AqiLevelService.java    # ★ AQI等级判定（通过IoC注入6种策略）
│               │   └── strategy/               # ★ Spring IoC 策略模式——AQI等级判定
│               │       ├── AqiLevelStrategy.java        #   策略接口
│               │       ├── ExcellentLevelStrategy.java  #   优 (0-50)
│               │       ├── GoodLevelStrategy.java       #   良 (51-100)
│               │       ├── MildPollutionStrategy.java   #   轻度污染 (101-150)
│               │       ├── ModeratePollutionStrategy.java # 中度污染 (151-200)
│               │       ├── HeavyPollutionStrategy.java  #   重度污染 (201-300)
│               │       └── SeverePollutionStrategy.java #   严重污染 (>300)
│               │
│               ├── controller/                 # 📦 REST API 控制器（10个）
│               │   ├── AuthController.java     #   登录/注册（JWT签发）
│               │   ├── UserController.java     #   用户管理（管理员）
│               │   ├── CityController.java     #   城市管理（管理员）
│               │   ├── AirQualityDataController.java # 空气质量数据查询/详情/趋势
│               │   ├── UserFavoriteController.java   # 收藏管理
│               │   ├── AlertThresholdController.java  # 预警阈值管理（管理员）
│               │   ├── OperationLogController.java    # 操作日志查询（管理员）
│               │   ├── AccessLogController.java       # 访问日志查询（管理员）
│               │   ├── SystemConfigController.java    # 系统配置
│               │   └── AirQualityArticleController.java # 科普资讯管理
│               │
│               ├── aop/                         # ★ Spring AOP 切面
│               │   ├── OperLog.java             #   自定义操作日志注解
│               │   ├── OperationLogAspect.java  #   拦截 @OperLog 自动记录操作日志
│               │   ├── AccessLogAspect.java     #   拦截所有 Controller 自动记录访问日志
│               │   └── AlertDetectionAspect.java #  拦截数据入库自动检测 AQI 预警
│               │
│               ├── security/                    # 🔐 安全认证
│               │   ├── JwtUtil.java             #   JWT 生成/解析/验证
│               │   ├── JwtAuthenticationFilter.java # 请求拦截解析 Token
│               │   └── SecurityConfig.java      #   Spring Security 配置（CORS/权限/放行）
│               │
│               └── common/                      # 📦 公共类
│                   ├── Result.java              #   统一API响应格式 {code, message, data}
│                   └── AqiLevelResult.java      #   AQI等级判定结果（等级名/颜色/健康提示）
│
├── air-quality-frontend/                        # 🖥️ Vue 3 前端项目
│   ├── package.json                             #   依赖配置
│   ├── vite.config.js                           #   Vite + 代理 + @别名
│   ├── index.html                               #   入口 HTML
│   └── src/
│       ├── main.js                              #   入口（加载 ElementPlus/Icons/路由/Pinia）
│       ├── App.vue                              #   根组件
│       │
│       ├── styles/
│       │   └── global.css                       #   全局样式（圆角/动画/渐变图案）
│       │
│       ├── api/                                 # 📡 接口层
│       │   ├── request.js                       #   Axios 封装（Token注入/错误拦截）
│       │   └── index.js                         #   全部API模块导出
│       │
│       ├── store/
│       │   └── user.js                          #   Pinia 用户状态（登录/登出/角色判断）
│       │
│       ├── router/
│       │   └── index.js                         #   Vue Router（路由守卫+角色拦截）
│       │
│       └── views/                               # 📄 页面组件
│           ├── Login.vue                        #   登录页（左品牌介绍+右表单）
│           ├── Register.vue                     #   注册页（左品牌介绍+右表单）
│           │
│           ├── layout/
│           │   └── Layout.vue                   #   后台布局（顶栏+侧栏+内容区）
│           │
│           ├── dashboard/
│           │   └── Dashboard.vue                #   首页（统计卡片+收藏AQI+预警+入口）
│           │
│           ├── air-quality/
│           │   ├── AirQualityList.vue           #   空气质量查询列表（城市+日期筛选）
│           │   └── AirQualityDetail.vue         #   单条数据详情
│           │
│           ├── charts/
│           │   └── Charts.vue                   #   数据可视化（趋势图+饼图+柱状图）
│           │
│           ├── favorites/
│           │   └── Favorites.vue                #   我的收藏（添加/删除城市）
│           │
│           ├── articles/
│           │   └── Articles.vue                 #   科普资讯列表
│           │
│           ├── profile/
│           │   └── Profile.vue                  #   个人中心（改昵称/改密码）
│           │
│           └── admin/                           # 👑 管理员页面
│               ├── Users.vue                    #   用户管理（启用/禁用/删除）
│               ├── Cities.vue                   #   城市管理（增删改查）
│               ├── AirData.vue                  #   数据管理（手动录入/编辑/删除）
│               ├── AlertThreshold.vue           #   预警阈值配置
│               ├── Articles.vue                 #   资讯发布管理
│               ├── OperationLogs.vue            #   操作日志查看
│               ├── AccessLogs.vue               #   访问日志查看
│               └── Config.vue                   #   系统基础配置
│
└── crawler/                                     # 🕷️ Python 爬虫
    ├── requirements.txt                         #   依赖（requests/bs4/pymysql/lxml）
    └── aqi_crawler.py                           #   从 tianqihoubao.com 采集→入库
```

## API 接口一览

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/login` | 用户登录（返回JWT） | 公开 |
| GET | `/api/cities/all` | 获取所有城市 | 公开 |
| GET | `/api/air-data` | 查询空气质量数据 | 公开 |
| GET | `/api/air-data/latest` | 获取城市最新数据 | 公开 |
| GET | `/api/air-data/favorites-latest` | 获取收藏城市最新数据 | 公开 |
| GET | `/api/air-data/trend` | 获取趋势数据 | 公开 |
| GET | `/api/air-data/aqi-level/{aqi}` | 获取AQI等级判定结果 | 公开 |
| GET | `/api/articles/published` | 获取已发布资讯 | 公开 |
| POST | `/api/air-data/batch` | 批量导入数据（爬虫用） | 需认证 |
| GET/POST/PUT/DELETE | `/api/users/**` | 用户管理 | 管理员 |
| GET/POST/PUT/DELETE | `/api/cities/**` | 城市管理（除 all） | 管理员 |
| GET/POST/PUT/DELETE | `/api/alert-thresholds/**` | 预警阈值管理 | 管理员 |
| GET | `/api/operation-logs` | 操作日志查询 | 管理员 |
| GET | `/api/access-logs` | 访问日志查询 | 管理员 |

## 启动指南

### 1. 数据库初始化

```bash
mysql -u root -p < database/init.sql
```

### 2. 启动后端

```bash
cd air-quality-backend/airquality
./mvnw spring-boot:run        # 端口 8080
```

### 3. 启动前端

```bash
cd air-quality-frontend
npm install
npm run dev                   # 端口 3000（已代理 /api 到 8080）
```

### 4. 运行爬虫（可选）

```bash
cd crawler
pip install -r requirements.txt
python aqi_crawler.py          # 采集昨天+今天的空气质量数据
```

### 5. 设置管理员

注册后用 MySQL 将用户角色改为 admin：

```sql
UPDATE air_quality_db.user SET role = 'admin' WHERE username = '你的邮箱';
```
