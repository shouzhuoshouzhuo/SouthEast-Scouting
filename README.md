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

## 快速运行

```powershell
Set-Location "D:\project\SouthEast-Scouting"
.\mvnw.cmd spring-boot:run
```

启动后访问：

- `http://localhost:8080/player-compare-demo.html`

## 测试

```powershell
Set-Location "D:\project\SouthEast-Scouting"
.\mvnw.cmd test
```

