# Spring Boot 项目骨架实施清单（单机版）

> 适用范围：`SouthEast-Scouting` 单机后端（模块化单体）  
> 目标：按此清单逐步落地 MVP（搜索、对比、导入）  
> 建议基线：Java 17 + Spring Boot 3.x + PostgreSQL + Redis

---

## 1. 项目结构（按包直接创建）

根包建议：`com.southeast.scouting`

```text
com.southeast.scouting
├─ ScoutingApplication.java
├─ common
│  ├─ api
│  │  ├─ ApiResponse.java
│  │  └─ ErrorCode.java
│  ├─ exception
│  │  ├─ BizException.java
│  │  └─ GlobalExceptionHandler.java
│  ├─ context
│  │  └─ TraceContextFilter.java
│  └─ util
│     ├─ HashUtils.java
│     └─ TimeUtils.java
├─ config
│  ├─ JacksonConfig.java
│  ├─ RedisConfig.java
│  ├─ OpenApiConfig.java
│  └─ WebMvcConfig.java
├─ infra
│  ├─ redis
│  │  ├─ CacheService.java
│  │  └─ DistributedLockService.java
│  └─ persistence
│     └─ BaseEntity.java
├─ player
│  ├─ controller
│  │  └─ PlayerController.java
│  ├─ service
│  │  └─ PlayerQueryService.java
│  ├─ repository
│  │  ├─ PlayerRepository.java
│  │  └─ PlayerSeasonStatRepository.java
│  ├─ entity
│  │  ├─ Player.java
│  │  ├─ PlayerSeasonStat.java
│  │  └─ PlayerMetricSnapshot.java
│  └─ dto
│     ├─ PlayerSearchRequest.java
│     ├─ PlayerSummaryDTO.java
│     └─ PlayerDetailDTO.java
├─ search
│  ├─ service
│  │  └─ SearchService.java
│  └─ dto
│     └─ SuggestionDTO.java
├─ compare
│  ├─ controller
│  │  └─ CompareController.java
│  ├─ service
│  │  └─ CompareService.java
│  └─ dto
│     ├─ CompareRequest.java
│     ├─ CompareMetricItemDTO.java
│     └─ CompareResponseDTO.java
├─ importing
│  ├─ controller
│  │  └─ ImportAdminController.java
│  ├─ service
│  │  ├─ ImportTaskService.java
│  │  ├─ CsvImportService.java
│  │  └─ ExcelImportService.java
│  ├─ repository
│  │  ├─ ImportTaskRepository.java
│  │  └─ ImportTaskErrorRepository.java
│  ├─ entity
│  │  ├─ ImportTask.java
│  │  └─ ImportTaskError.java
│  └─ dto
│     ├─ ImportCreateResponseDTO.java
│     ├─ ImportTaskStatusDTO.java
│     └─ ImportErrorDTO.java
└─ jobs
   ├─ MetricSnapshotJob.java
   └─ CacheWarmupJob.java
```

---

## 2. Maven 依赖（MVP 必需）

`pom.xml` 至少包含：

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-actuator`
- `springdoc-openapi-starter-webmvc-ui`
- `postgresql`
- `flyway-core`
- `bucket4j-spring-boot-starter`（限流）
- `micrometer-registry-prometheus`
- `lombok`（可选）
- `poi-ooxml`（Excel）
- `commons-csv`（CSV）

---

## 3. 数据库 DDL 清单（先做 Flyway）

迁移脚本建议：

- `V1__init_player_tables.sql`
- `V2__init_import_tables.sql`
- `V3__add_indexes.sql`

### 3.1 核心表

1) `player`
- `id`（PK）
- `name`
- `league`
- `team`
- `position`
- `age`
- `created_at`
- `updated_at`

2) `player_season_stat`
- `id`（PK）
- `player_id`（FK）
- `season`
- `minutes`
- `goals`
- `assists`
- `xg`
- `key_passes`
- `tackles`
- `interceptions`
- `created_at`
- `updated_at`

3) `player_metric_snapshot`
- `id`（PK）
- `player_id`（FK）
- `season`
- `metric_version`
- `attack_score`
- `passing_score`
- `defense_score`
- `overall_score`
- `generated_at`

### 3.2 导入表

4) `import_task`
- `id`（PK）
- `file_name`
- `file_hash`（唯一）
- `status`（PENDING/RUNNING/SUCCESS/FAILED）
- `total_rows`
- `success_rows`
- `failed_rows`
- `error_message`
- `started_at`
- `finished_at`
- `created_at`

5) `import_task_error`
- `id`（PK）
- `task_id`（FK）
- `row_no`
- `column_name`
- `raw_value`
- `reason`
- `created_at`

### 3.3 索引

- `idx_player_name`
- `idx_player_league_team_position`
- `idx_stat_player_season`
- `uk_snapshot_player_season_version`
- `uk_import_task_file_hash`

---

## 4. API 契约（先稳定接口再写前端联调）

### 4.1 查询与搜索

- `GET /api/players/search`
  - Query: `q`, `league`, `team`, `position`, `page`, `size`
  - Response: `ApiResponse<Page<PlayerSummaryDTO>>`

- `GET /api/players/{id}`
  - Response: `ApiResponse<PlayerDetailDTO>`

### 4.2 球员对比

- `POST /api/players/compare`
  - Body: `CompareRequest{ playerIds, season, metricVersion }`
  - Response: `ApiResponse<CompareResponseDTO>`

### 4.3 导入管理

- `POST /api/admin/import`
  - FormData: `file`
  - Response: `ApiResponse<ImportCreateResponseDTO>`

- `GET /api/admin/import/{taskId}`
  - Response: `ApiResponse<ImportTaskStatusDTO>`

---

## 5. DTO 与校验规则（必须先定）

### 5.1 `PlayerSearchRequest`
- `q`：可空，最大长度 64
- `league/team/position`：可空，最大长度 32
- `page`：>=0
- `size`：1~50

### 5.2 `CompareRequest`
- `playerIds`：数量 2~6
- `season`：非空
- `metricVersion`：默认 `v1`

### 5.3 `Import` 约束
- 文件类型：`csv/xlsx`
- 文件大小：建议 <= 20MB
- 行数软限制：<= 1 万（超限直接失败并给提示）

---

## 6. 并发控制实现清单（按优先级）

1. 导入锁：`lock:import`
- 获取锁成功才进入导入
- 锁超时自动释放，避免死锁

2. 预计算任务锁：`lock:metric_job`
- 定时任务单实例执行

3. 幂等键：`file_hash`
- 同文件重复上传不重复处理

4. 热点 Key 互斥回源
- 对比接口缓存失效时，只允许一个线程回源 DB

---

## 7. 配置文件模板（`application.yml`）

建议分环境：

- `application-dev.yml`
- `application-prod.yml`

核心配置项：

- `spring.datasource.*`
- `spring.data.redis.*`
- `spring.jpa.*`
- `spring.flyway.*`
- `management.endpoints.web.exposure.include=health,metrics,prometheus`
- `management.prometheus.metrics.export.enabled=true`
- 自定义：
  - `app.cache.ttl.search-seconds`
  - `app.cache.ttl.compare-seconds`
  - `app.import.max-rows`
  - `app.import.allowed-types`
  - `app.lock.import-expire-seconds`

---

## 8. 缓存 Key 规范（先统一命名）

- 搜索建议：`search:suggest:{q}:{league}:{team}:{position}`
- 球员详情：`player:detail:{playerId}:{season}`
- 对比结果：`compare:{season}:{playerIdsHash}:{metricVersion}`
- 热门榜单：`hot:players:{season}`

---

## 9. 日志与可观测（MVP 最低要求）

1. 每个请求注入 `traceId`
2. 统一日志字段：`traceId`, `path`, `costMs`, `status`, `errorCode`
3. 指标：
- 接口耗时与 QPS
- 缓存命中率
- 导入成功率/失败率
- 锁等待时间

---

## 10. 测试清单（首批必须）

### 10.1 单元测试
- `CompareService` 指标聚合正确性
- `ImportTaskService` 状态机流转

### 10.2 集成测试
- 导入一份 1000 行 CSV，验证成功行与错误行
- 搜索接口筛选组合查询
- 对比接口缓存命中与回源行为

### 10.3 压测（可选）
- `search`：并发 100，P95 < 100ms
- `compare`：并发 50，P95 < 200ms

---

## 11. Docker Compose（单机交付）

至少包含 4 个服务：

- `app`（Spring Boot）
- `postgres`
- `redis`
- `prometheus`（可选）
- `grafana`（可选）

说明：首版不必引入消息队列，避免复杂度失控。

---

## 12. 开发执行顺序（按天拆分）

### D1-D2
- 初始化 Spring Boot 工程
- 接入 PostgreSQL + Flyway
- 创建 `player` 基础表与查询接口

### D3-D5
- 完成搜索接口（模糊 + 筛选 + 分页）
- 加入 Redis 缓存

### D6-D9
- 完成导入模块（CSV 优先）
- 加入导入任务表、错误表、分布式锁、幂等控制

### D10-D12
- 完成对比接口
- 接入 `player_metric_snapshot` 与缓存

### D13-D14
- 加入监控、日志、限流
- 补充测试与文档

---

## 13. 里程碑验收标准（你是否能继续下一阶段）

满足以下条件即可进入“功能扩展阶段”：

- 能稳定导入并追踪错误
- 搜索接口稳定返回且可筛选
- 对比接口可返回固定指标并命中缓存
- 基础监控可看到耗时、错误率、缓存命中率

---

## 14. 下一步建议（紧接当前清单）

1. 先落 `V1/V2/V3` 三个 Flyway 脚本  
2. 然后实现 `search + import` 两条核心链路  
3. 最后再做 `compare + snapshot + metrics`  

这样推进最稳，且每周都有可演示成果。
