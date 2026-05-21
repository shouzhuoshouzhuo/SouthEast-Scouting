# SouthEast-Scouting

球员对比前端 Demo（V1）已实现为一个 Spring Boot 静态页面，核心功能包括：

- 联赛 / 赛季 / 位置筛选
- 球员模糊搜索 + 自动补全（支持键盘上下选择、Enter 确认、Esc 关闭）
- 最多 3 名球员对比与移除
- 雷达图叠加对比（ECharts，支持图例联动隐藏/显示）
- 空状态 / 加载状态 / 异常状态（含重试按钮）

## 目录

- 页面入口：`src/main/resources/static/player-compare-demo.html`
- 样式文件：`src/main/resources/static/css/player-compare-demo.css`
- Mock 数据：`src/main/resources/static/js/mock-data.js`
- 交互逻辑：`src/main/resources/static/js/player-compare-demo.js`
- 数据库迁移脚本：`src/main/resources/db/migration/V1__init_schema.sql`、`src/main/resources/db/migration/V2__add_indexes.sql`

## Docker 数据库初始化（首次部署必须执行）

项目启动时会连接 PostgreSQL，并在启动过程中读取 `player_season_stat` 等业务表。如果 Docker 数据库中尚未执行建表脚本，启动会失败，并出现类似错误：

```text
ERROR: relation "player_season_stat" does not exist
SQL [select count(*) from player_season_stat pss1_0]
```

因此，首次部署或重建 Docker volume 后，需要先将 `migration` 目录下的 SQL 脚本执行到 Docker PostgreSQL 数据库中。

### 1. 确认 Docker 容器正在运行

```powershell
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}"
```

正常应看到类似结果：

```text
NAMES         IMAGE         PORTS
se_postgres   postgres:16   0.0.0.0:5432->5432/tcp
se_redis      redis:7       0.0.0.0:6379->6379/tcp
```

本项目默认使用：

- PostgreSQL 容器名：`se_postgres`
- PostgreSQL 用户名：`se_user`
- 数据库名：`southeast_sc`
- 宿主机端口：`5432`

如果本地环境中的容器名、用户名或数据库名不同，请相应替换下面命令中的参数。

> 注意：如果主机端曾经安装过本地 PostgreSQL，本地 PostgreSQL 服务可能已经占用 `5432` 端口，导致 Docker PostgreSQL 无法正常绑定 `5432`，或者 Spring Boot 实际连接到错误的数据库实例。建议部署前卸载本地 PostgreSQL；如果不能卸载，至少需要停止本地 PostgreSQL 服务，并确认 `docker ps` 中 `se_postgres` 显示为 `0.0.0.0:5432->5432/tcp`。

### 2. 确认能够连接到目标数据库

```powershell
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "SELECT current_database(), current_schema();"
```

正常应返回：

```text
 current_database | current_schema
------------------+----------------
 southeast_sc     | public
```

如果提示 `role "postgres" does not exist`，说明不能使用 `-U postgres`，应使用本项目 Docker 配置中的用户 `se_user`。

### 3. 检查当前数据库是否已有业务表

```powershell
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "\dt"
```

如果返回：

```text
Did not find any relations.
```

说明数据库是空的，需要执行下面的迁移脚本。

### 4. 执行 V1 建表脚本

在项目根目录执行：

```powershell
Get-ChildItem -Recurse -Filter "V1*.sql" | Select-Object -First 1 | ForEach-Object {
    Get-Content -Raw $_.FullName | docker exec -i se_postgres psql -U se_user -d southeast_sc
}
```

成功后会看到类似输出：

```text
CREATE TABLE
CREATE TABLE
CREATE TABLE
```

### 5. 执行 V2 索引脚本

继续在项目根目录执行：

```powershell
Get-ChildItem -Recurse -Filter "V2*.sql" | Select-Object -First 1 | ForEach-Object {
    Get-Content -Raw $_.FullName | docker exec -i se_postgres psql -U se_user -d southeast_sc
}
```

成功后会看到类似输出：

```text
CREATE EXTENSION
CREATE INDEX
CREATE INDEX
```

如果部分索引或扩展已经存在，提示 `already exists` 一般不影响继续运行。

### 6. 验证表是否创建成功

```powershell
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "\dt"
```

正常应至少看到以下 8 张表：

```text
competition
import_task
import_task_error
player
player_metric_snapshot
player_season_stat
team
team_match_stat
```

也可以单独验证关键表：

```powershell
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "SELECT count(*) FROM player_season_stat;"
```

只要不再出现 `relation "player_season_stat" does not exist`，说明数据库初始化成功。

## 快速运行

首次运行前，请先完成上面的 Docker 数据库初始化步骤。

```powershell
Set-Location "D:\project\SouthEast-Scouting"
.\mvnw.cmd spring-boot:run
```

如果当前目录已经是项目根目录，也可以直接运行：

```powershell
mvn spring-boot:run
```

启动后访问：

- `http://localhost:8080/player-compare-demo.html`

## 常见问题

下面是执行 `mvn spring-boot:run` 后最常见的两个启动问题：一个是主机端 PostgreSQL 占用了 `5432` 端口，导致项目没有连到 Docker PostgreSQL；另一个是 Docker 数据库中还没有建立业务表。

### 1. PostgreSQL 端口被本地服务占用，导致数据库连接异常

典型报错片段如下：

```text
WARN  ... SQLState: 28P01
WARN  ... "se_user" Password ...
org.hibernate.exception.AuthException: Unable to obtain isolated JDBC connection
Unable to determine Dialect without JDBC metadata
```

原因：项目默认连接：

```text
jdbc:postgresql://localhost:5432/southeast_sc
```

如果主机端已经安装并启动了本地 PostgreSQL，它可能占用了 `5432` 端口。此时 Spring Boot 可能连接到了本机 PostgreSQL，而不是 Docker 容器 `se_postgres` 中的 PostgreSQL，因此会出现密码认证失败、无法获取 JDBC 连接、Hibernate 无法判断 Dialect 等错误。

建议处理方式：

1. 建议卸载主机端本地 PostgreSQL，避免和 Docker PostgreSQL 端口冲突。
2. 如果暂时不能卸载，至少先停止本地 PostgreSQL 服务。
3. 重新检查 Docker PostgreSQL 是否占用了 `5432`：

```powershell
docker ps --format "table {{.Names}}	{{.Image}}	{{.Ports}}"
```

正常应看到：

```text
se_postgres   postgres:16   0.0.0.0:5432->5432/tcp
```

如果看到 Docker 映射成 `5433->5432/tcp`，则需要同步修改 Spring Boot 的数据库连接端口。

### 2. 数据库表未建立，导致 `player_season_stat` 不存在

典型报错片段如下：

```text
ERROR: relation "player_season_stat" does not exist
SQL [select count(*) from player_season_stat pss1_0]
at com.southeast.scouting.player.service.SampleDataImporter.run(SampleDataImporter.java:65)
```

原因：Spring Boot 已经成功连接到 Docker PostgreSQL，但 `southeast_sc.public` 中还没有执行建表脚本，所以启动时查询 `player_season_stat` 表会失败。

处理方式：在项目根目录执行 `V1__init_schema.sql` 和 `V2__add_indexes.sql`。

先检查当前数据库是否有表：

```powershell
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "\dt"
```

如果显示：

```text
Did not find any relations.
```

说明表还没有建立。继续执行：

```powershell
Get-ChildItem -Recurse -Filter "V1*.sql" | Select-Object -First 1 | ForEach-Object {
    Get-Content -Raw $_.FullName | docker exec -i se_postgres psql -U se_user -d southeast_sc
}

Get-ChildItem -Recurse -Filter "V2*.sql" | Select-Object -First 1 | ForEach-Object {
    Get-Content -Raw $_.FullName | docker exec -i se_postgres psql -U se_user -d southeast_sc
}
```

执行完成后验证表是否存在：

```powershell
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "\dt"
docker exec -it se_postgres psql -U se_user -d southeast_sc -c "SELECT count(*) FROM player_season_stat;"
```

正常应能看到以下业务表：

```text
competition
import_task
import_task_error
player
player_metric_snapshot
player_season_stat
team
team_match_stat
```

确认 `player_season_stat` 能正常查询后，再重新启动项目：

```powershell
mvn spring-boot:run
```

## 测试

```powershell
Set-Location "D:\project\SouthEast-Scouting"
.\mvnw.cmd test
```
