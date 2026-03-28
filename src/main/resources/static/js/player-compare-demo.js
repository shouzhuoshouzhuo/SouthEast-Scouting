(function () {
    const palette = ['#ff3b30', '#ccff00', '#5ac8fa', '#ffcc00']; // Red, Lime, Blue, Yellow - Jersey Colors

    const state = {
        filters: { league: "全部", season: "全部", position: "MF" },
        query: "",
        selectedPlayers: [],
        suggestions: [],
        activeSuggestionIndex: -1,
        loadingTimer: null,
        chart: null,
        hasError: false
    };

    let dom = {};

    const data = window.ScoutingMockData;

    function cacheDomRefs() {
        dom = {
            leagueSelect: document.getElementById("leagueSelect"),
            seasonSelect: document.getElementById("seasonSelect"),
            positionSelect: document.getElementById("positionSelect"),
            playerSearchInput: document.getElementById("playerSearchInput"),
            suggestionList: document.getElementById("suggestionList"),
            selectedCount: document.getElementById("selectedCount"),
            warningText: document.getElementById("warningText"),
            playerCards: document.getElementById("playerCards"),
            stateBox: document.getElementById("stateBox"),
            radarChart: document.getElementById("radarChart"),
            metricList: document.getElementById("metricList"),
            retryButton: document.getElementById("retryButton"),
            interactiveBall: document.getElementById("interactiveBall")
        };
    }

    function initChart() {
        if (!dom.radarChart) {
            setError("图表容器不存在，无法渲染雷达图。");
            return false;
        }
        if (!window.echarts) {
            setError("图表组件加载失败，请检查网络后点击重试。");
            return false;
        }
        if (state.chart) {
            return true;
        }
        // Do not use dark theme here, otherwise clear() leaves a dark purple fallback background.
        state.chart = window.echarts.init(dom.radarChart, null, { renderer: 'canvas' });
        window.addEventListener("resize", function () {
            if (state.chart) {
                state.chart.resize();
            }
        });
        clearError();
        return true;
    }

    function init() {
        cacheDomRefs();
        if (!data || !Array.isArray(data.players)) {
            setError("初始化失败：未找到 mock 数据。");
            return;
        }
        fillSelect(dom.leagueSelect, data.leagues, state.filters.league);
        fillSelect(dom.seasonSelect, data.seasons, state.filters.season);
        fillSelect(dom.positionSelect, data.positions, state.filters.position);
        bindEvents();
        bindSoccerBall();
        renderMetricList();
        initChart();
        renderAll();
    }

    function bindSoccerBall() {
        if (!dom.interactiveBall) {
            return;
        }

        const ball = dom.interactiveBall;
        const reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        let targetX = 0;
        let targetY = 0;
        let currentX = 0;
        let currentY = 0;
        let rafId = null;

        function clamp(value, min, max) {
            return Math.max(min, Math.min(max, value));
        }

        function renderFollow() {
            if (ball.classList.contains('kicked')) {
                rafId = null;
                return;
            }
            currentX += (targetX - currentX) * 0.16;
            currentY += (targetY - currentY) * 0.16;
            ball.style.transform = 'translate(' + currentX.toFixed(1) + 'px, ' + currentY.toFixed(1) + 'px) rotate(' + (currentX * -0.4).toFixed(1) + 'deg)';
            if (Math.abs(targetX - currentX) > 0.1 || Math.abs(targetY - currentY) > 0.1) {
                rafId = window.requestAnimationFrame(renderFollow);
            } else {
                rafId = null;
            }
        }

        function createKickBurst() {
            const rect = ball.getBoundingClientRect();
            const originX = rect.left + rect.width / 2;
            const originY = rect.top + rect.height / 2;
            for (let i = 0; i < 14; i += 1) {
                const dot = document.createElement('span');
                dot.className = 'ball-trail';
                dot.style.left = originX + 'px';
                dot.style.top = originY + 'px';
                const angle = (Math.PI * 2 * i) / 14;
                const distance = 45 + Math.random() * 80;
                dot.style.setProperty('--dx', Math.cos(angle) * distance + 'px');
                dot.style.setProperty('--dy', Math.sin(angle) * distance + 'px');
                document.body.appendChild(dot);
                dot.addEventListener('animationend', function () {
                    dot.remove();
                });
            }
        }

        if (!reducedMotion) {
            document.addEventListener('mousemove', function (event) {
                if (ball.classList.contains('kicked')) {
                    return;
                }
                const normalizedX = (event.clientX / window.innerWidth - 0.5) * -1;
                const normalizedY = (event.clientY / window.innerHeight - 0.5) * -1;
                targetX = clamp(normalizedX * 18, -18, 18);
                targetY = clamp(normalizedY * 18, -18, 18);
                if (!rafId) {
                    rafId = window.requestAnimationFrame(renderFollow);
                }
            });
        }

        function kickBall() {
            if (ball.classList.contains('kicked')) {
                return;
            }
            createKickBurst();
            ball.classList.add('kicked');
            window.setTimeout(function () {
                ball.classList.remove('kicked');
                targetX = 0;
                targetY = 0;
                if (!rafId) {
                    rafId = window.requestAnimationFrame(renderFollow);
                }
            }, 900);
        }

        ball.addEventListener('click', kickBall);
        ball.addEventListener('keydown', function (event) {
            if (event.key === 'Enter' || event.key === ' ') {
                event.preventDefault();
                kickBall();
            }
        });
    }

    function fillSelect(selectEl, values, defaultValue) {
        selectEl.innerHTML = values.map((v) => "<option value=\"" + v + "\">" + v + "</option>").join("");
        selectEl.value = defaultValue;
    }

    function bindEvents() {
        dom.leagueSelect.addEventListener("change", function (event) {
            state.filters.league = event.target.value;
            state.query = "";
            dom.playerSearchInput.value = "";
            clearSuggestions();
            renderAll();
        });

        dom.seasonSelect.addEventListener("change", function (event) {
            state.filters.season = event.target.value;
            state.query = "";
            dom.playerSearchInput.value = "";
            clearSuggestions();
            renderAll();
        });

        dom.positionSelect.addEventListener("change", function (event) {
            state.filters.position = event.target.value;
            state.query = "";
            dom.playerSearchInput.value = "";
            clearSuggestions();
            renderAll();
        });

        dom.playerSearchInput.addEventListener("input", function (event) {
            state.query = event.target.value.trim();
            state.activeSuggestionIndex = -1;
            renderSuggestions();
        });

        dom.playerSearchInput.addEventListener("keydown", function (event) {
            if (dom.suggestionList.classList.contains("hidden")) {
                return;
            }
            if (event.key === "ArrowDown") {
                event.preventDefault();
                state.activeSuggestionIndex = Math.min(state.activeSuggestionIndex + 1, state.suggestions.length - 1);
                renderSuggestions();
            } else if (event.key === "ArrowUp") {
                event.preventDefault();
                state.activeSuggestionIndex = Math.max(state.activeSuggestionIndex - 1, 0);
                renderSuggestions();
            } else if (event.key === "Enter") {
                event.preventDefault();
                if (state.activeSuggestionIndex >= 0 && state.suggestions[state.activeSuggestionIndex]) {
                    addPlayer(state.suggestions[state.activeSuggestionIndex]);
                }
            } else if (event.key === "Escape") {
                clearSuggestions();
            }
        });

        document.addEventListener("click", function (event) {
            const clickInsideSearch = event.target === dom.playerSearchInput || dom.suggestionList.contains(event.target);
            if (!clickInsideSearch) {
                clearSuggestions();
            }
        });

        dom.retryButton.addEventListener("click", function () {
            state.hasError = false;
            if (initChart()) {
                renderAll();
            }
        });
    }

    function filterByConditions(player) {
        const hitLeague = state.filters.league === "全部" || player.league === state.filters.league;
        const hitSeason = state.filters.season === "全部" || player.season === state.filters.season;
        const hitPosition = state.filters.position === "全部" || player.position === state.filters.position;
        return hitLeague && hitSeason && hitPosition;
    }

    function getFilteredPlayers() {
        return data.players.filter(filterByConditions);
    }

    function renderSuggestions() {
        if (!state.query) {
            clearSuggestions();
            return;
        }

        const params = new URLSearchParams({ q: state.query });
        if (state.filters.league !== '全部') params.append('league', state.filters.league);
        if (state.filters.season !== '全部') params.append('season', state.filters.season);
        if (state.filters.position !== '全部') params.append('position', state.filters.position);

        fetch('/api/players/search?' + params)
            .then(function (response) {
                return response.json();
            })
            .then(function (results) {
                const selectedIds = new Set(state.selectedPlayers.map((p) => p.playerId));
                state.suggestions = results
                    .filter(function (r) {
                        return !selectedIds.has(r.playerId);
                    })
                    .map(function (r) {
                        return {
                            playerId: r.playerId,
                            playerName: r.playerName,
                            teamName: r.teamName,
                            league: r.league,
                            season: r.season,
                            position: r.position,
                            compareContext: r.teamName,
                            metrics: {}
                        };
                    });

                if (state.suggestions.length === 0) {
                    clearSuggestions();
                    return;
                }

                dom.suggestionList.innerHTML = state.suggestions.map(function (p, index) {
                    const activeClass = index === state.activeSuggestionIndex ? "active" : "";
                    return "<li class=\"suggestion-item " + activeClass + "\" data-id=\"" + p.playerId + "\">"
                        + "<div>" + p.playerName + "</div>"
                        + "<small>" + p.teamName + " | " + p.league + " | " + p.season + "</small>"
                        + "</li>";
                }).join("");

                dom.suggestionList.querySelectorAll(".suggestion-item").forEach(function (item) {
                    item.addEventListener("click", function () {
                        const candidate = state.suggestions.find((p) => p.playerId === item.getAttribute("data-id"));
                        if (candidate) {
                            addPlayer(candidate);
                        }
                    });
                });

                dom.suggestionList.classList.remove("hidden");
            })
            .catch(function (error) {
                console.error('Search failed:', error);
                clearSuggestions();
            });
    }

    function clearSuggestions() {
        state.suggestions = [];
        state.activeSuggestionIndex = -1;
        dom.suggestionList.innerHTML = "";
        dom.suggestionList.classList.add("hidden");
    }

    function addPlayer(player) {
        if (state.selectedPlayers.length >= 3) {
            dom.warningText.textContent = "最多选择 3 人，请先移除后再添加。";
            return;
        }

        dom.warningText.textContent = "";
        state.selectedPlayers.push(player);
        state.query = "";
        dom.playerSearchInput.value = "";
        clearSuggestions();
        renderAll();
    }

    function removePlayer(playerId) {
        state.selectedPlayers = state.selectedPlayers.filter((p) => p.playerId !== playerId);
        dom.warningText.textContent = "";
        renderAll();
    }

    function renderCards() {
        dom.selectedCount.textContent = "已选 " + state.selectedPlayers.length + " / 3";
        if (state.selectedPlayers.length === 0) {
            dom.playerCards.innerHTML = "";
            return;
        }

        dom.playerCards.innerHTML = state.selectedPlayers.map(function (player, index) {
            const color = palette[index % palette.length];
            return "<article class=\"player-card\">"
                + "<div class=\"color-bar\" style=\"background:" + color + "\"></div>"
                + "<div class=\"player-row\">"
                + "<h3 class=\"player-name\">" + player.playerName + "</h3>"
                + "<button class=\"remove-btn\" data-id=\"" + player.playerId + "\" type=\"button\">移除</button>"
                + "</div>"
                + "<p class=\"player-meta\">" + player.compareContext + "<br/>"
                + player.season + " " + player.league + "<br/>"
                + player.teamName + " | " + player.position + "</p>"
                + "</article>";
        }).join("");

        dom.playerCards.querySelectorAll(".remove-btn").forEach(function (btn) {
            btn.addEventListener("click", function () {
                removePlayer(btn.getAttribute("data-id"));
            });
        });
    }

    function renderMetricList() {
        dom.metricList.innerHTML = "<h3>指标说明</h3>" + data.metricCatalog.map(function (metric) {
            return "<div class=\"metric-item\">"
                + "<div class=\"metric-label\">" + metric.label + "</div>"
                + "<div class=\"metric-desc\">" + metric.desc + "（0-100 百分位）</div>"
                + "</div>";
        }).join("");
    }

    function showState(text) {
        dom.stateBox.textContent = text;
    }

    function setError(message) {
        state.hasError = true;
        showState(message);
        dom.retryButton.classList.remove("hidden");
    }

    function clearError() {
        state.hasError = false;
        dom.retryButton.classList.add("hidden");
    }

    function renderRadar() {
        if (!state.chart) {
            if (!state.hasError) {
                showState("图表组件未就绪，请点击“重试”。");
                dom.retryButton.classList.remove("hidden");
            }
            return;
        }

        if (state.selectedPlayers.length === 0) {
            showState("空状态：请先选择至少 1 名球员。");
            state.chart.clear();
            // Keep the chart area transparent in empty state.
            state.chart.setOption({
                backgroundColor: 'transparent',
                animation: false,
                series: []
            }, true);
            return;
        }

        showState("加载中：正在刷新雷达图...");

        if (state.loadingTimer) {
            window.clearTimeout(state.loadingTimer);
        }

        state.loadingTimer = window.setTimeout(function () {
            try {
                const indicators = data.metricCatalog.map((metric) => ({ name: metric.label, max: 100 }));
                const seriesData = state.selectedPlayers.map(function (player, idx) {
                    const values = data.metricCatalog.map((metric) => player.metrics[metric.key] || 0);
                    return {
                        value: values,
                        name: player.playerName + " (" + player.season + ")",
                        areaStyle: { color: hexToRgba(palette[idx], 0.2) },
                        lineStyle: { width: 2, color: palette[idx] },
                        itemStyle: { color: palette[idx] }
                    };
                });

                var option = {
                    backgroundColor: 'transparent',
                    color: palette,
                    tooltip: {
                        trigger: 'item',
                        backgroundColor: 'rgba(15, 20, 32, 0.9)',
                        borderColor: '#232b3e',
                        textStyle: {
                            color: '#fff',
                            fontFamily: 'Outfit'
                        }
                    },
                    legend: {
                        data: seriesData.map(d => d.name),
                        bottom: 5,
                        textStyle: {
                            color: '#8b99ae',
                            fontFamily: 'Outfit',
                            fontSize: 14
                        }
                    },
                    radar: {
                        indicator: indicators,
                        shape: 'polygon',
                        splitNumber: 5,
                        axisName: {
                            color: '#f3ffcf',
                            fontFamily: 'Outfit',
                            fontSize: 13,
                            fontWeight: 600,
                            formatter: function (val) {
                                return val;
                            }
                        },
                        splitLine: {
                            lineStyle: {
                                color: [
                                    'rgba(204, 255, 0, 0.25)',
                                    'rgba(204, 255, 0, 0.22)',
                                    'rgba(204, 255, 0, 0.19)',
                                    'rgba(204, 255, 0, 0.16)',
                                    'rgba(204, 255, 0, 0.13)'
                                ]
                            }
                        },
                        splitArea: {
                            show: true,
                            areaStyle: {
                                color: [
                                    'rgba(204, 255, 0, 0.03)',
                                    'rgba(255, 255, 255, 0.015)'
                                ]
                            }
                        },
                        axisLine: {
                            lineStyle: {
                                color: 'rgba(204, 255, 0, 0.22)'
                            }
                        }
                    },
                    series: [{
                        name: '能力对比 (Percentile Rank)',
                        type: 'radar',
                        data: seriesData,
                        symbol: 'circle',
                        symbolSize: 6,
                        itemStyle: {
                            borderWidth: 2
                        },
                        lineStyle: {
                            width: 3
                        },
                        areaStyle: {
                            opacity: 0.15
                        }
                    }]
                };

                state.chart.setOption(option, true);
                showState("已加载：可点击图例临时隐藏/显示球员曲线。");
                clearError();
            } catch (err) {
                setError("数据异常：图表渲染失败，请重试。");
            }
        }, 350);
    }

    function renderAll() {
        if (state.hasError) {
            return;
        }
        const availablePlayers = getFilteredPlayers();
        if (availablePlayers.length === 0) {
            showState("空状态：当前筛选条件下没有可选球员。请调整联赛/赛季/位置。");
        }
        renderCards();
        renderRadar();
    }

    function hexToRgba(hex, alpha) {
        const normalized = hex.replace("#", "");
        const bigint = parseInt(normalized, 16);
        const r = (bigint >> 16) & 255;
        const g = (bigint >> 8) & 255;
        const b = bigint & 255;
        return "rgba(" + r + "," + g + "," + b + "," + alpha + ")";
    }

    document.addEventListener("DOMContentLoaded", init);
})();
