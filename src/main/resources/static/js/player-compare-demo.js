(function () {
    const palette = ['#ff3b30', '#ccff00', '#5ac8fa', '#ffcc00'];

    const METRIC_CATALOG = [
        { key: 'shotsPer90',                      label: 'Shots /90',               group: 'attacking'  },
        { key: 'shotsOnTargetPct',                label: 'Shots on Target %',       group: 'attacking'  },
        { key: 'xaPer90',                         label: 'xA /90',                  group: 'attacking'  },
        { key: 'assistsPer90',                    label: 'Assists /90',             group: 'attacking'  },
        { key: 'xgPer90',                         label: 'xG /90',                  group: 'attacking'  },
        { key: 'nonPenGoalsPer90',                label: 'Non-Pen Goals /90',       group: 'attacking'  },
        { key: 'passesPer90',                     label: 'Passes /90',              group: 'possession' },
        { key: 'accuratePassesPct',               label: 'Accurate Passes %',       group: 'possession' },
        { key: 'progressivePassesPer90',          label: 'Progressive Passes /90',  group: 'possession' },
        { key: 'accurateProgressivePassesPct',    label: 'Accurate Prog. Passes %', group: 'possession' },
        { key: 'touchesInBoxPer90',               label: 'Touches in Box /90',      group: 'possession' },
        { key: 'successfulDribblesPct',           label: 'Successful Dribbles %',   group: 'possession' },
        { key: 'slidingTacklesPer90',             label: 'Sliding Tackles /90',     group: 'defending'  },
        { key: 'interceptionsPer90',              label: 'Interceptions /90',       group: 'defending'  },
        { key: 'shotsBlockedPer90',               label: 'Shots Blocked /90',       group: 'defending'  },
        { key: 'aerialDuelsWonPct',               label: 'Aerial Duels Won %',      group: 'defending'  },
        { key: 'successfulDefensiveActionsPer90', label: 'Def. Actions /90',        group: 'defending'  }
    ];

    const GROUP_COLORS = { attacking: '#ff6b6b', possession: '#5ac8fa', defending: '#52c41a' };

    const state = {
        filters: { league: '全部', season: '全部', position: '全部' },
        query: '',
        selectedPlayers: [],
        compareData: null,
        suggestions: [],
        activeSuggestionIndex: -1,
        loadingTimer: null,
        chart: null,
        hasError: false
    };

    let dom = {};

    function cacheDomRefs() {
        dom = {
            leagueSelect:       document.getElementById('leagueSelect'),
            seasonSelect:       document.getElementById('seasonSelect'),
            positionSelect:     document.getElementById('positionSelect'),
            playerSearchInput:  document.getElementById('playerSearchInput'),
            suggestionList:     document.getElementById('suggestionList'),
            selectedCount:      document.getElementById('selectedCount'),
            warningText:        document.getElementById('warningText'),
            playerCards:        document.getElementById('playerCards'),
            stateBox:           document.getElementById('stateBox'),
            radarChart:         document.getElementById('radarChart'),
            metricList:         document.getElementById('metricList'),
            retryButton:        document.getElementById('retryButton'),
            advancedAnalysisBtn:document.getElementById('advancedAnalysisBtn'),
            toastContainer:     document.getElementById('toastContainer'),
            interactiveBall:    document.getElementById('interactiveBall')
        };
    }

    function initChart() {
        if (!dom.radarChart) { setError('图表容器不存在，无法渲染雷达图。'); return false; }
        if (!window.echarts) { setError('图表组件加载失败，请检查网络后点击重试。'); return false; }
        if (state.chart) { return true; }
        state.chart = window.echarts.init(dom.radarChart, null, { renderer: 'canvas' });
        window.addEventListener('resize', function () { if (state.chart) state.chart.resize(); });
        clearError();
        return true;
    }

    function init() {
        cacheDomRefs();
        fillSelect(dom.leagueSelect,   ['全部', 'Bundesliga', 'Premier League', 'La Liga', 'Serie A', 'Ligue 1'], '全部');
        fillSelect(dom.seasonSelect,   ['全部', '22-23'], '全部');
        fillSelect(dom.positionSelect, ['全部', 'GK', 'CB', 'LB', 'RB', 'DMF', 'AMF', 'CF', 'LW', 'RW'], '全部');
        bindEvents();
        bindSoccerBall();
        renderMetricList();
        initChart();
        renderAll();
    }

    function fillSelect(selectEl, values, defaultValue) {
        if (!selectEl) return;
        selectEl.innerHTML = values.map((v) => '<option value="' + v + '">' + v + '</option>').join('');
        selectEl.value = defaultValue;
    }

    function bindEvents() {
        dom.leagueSelect.addEventListener('change', function (e) {
            state.filters.league = e.target.value;
            state.query = ''; dom.playerSearchInput.value = ''; clearSuggestions();
        });
        dom.seasonSelect.addEventListener('change', function (e) {
            state.filters.season = e.target.value;
            state.query = ''; dom.playerSearchInput.value = ''; clearSuggestions();
        });
        dom.positionSelect.addEventListener('change', function (e) {
            state.filters.position = e.target.value;
            state.query = ''; dom.playerSearchInput.value = ''; clearSuggestions();
        });

        dom.playerSearchInput.addEventListener('input', function (e) {
            state.query = e.target.value.trim();
            state.activeSuggestionIndex = -1;
            renderSuggestions();
        });

        dom.playerSearchInput.addEventListener('keydown', function (e) {
            if (dom.suggestionList.classList.contains('hidden')) return;
            if (e.key === 'ArrowDown') {
                e.preventDefault();
                state.activeSuggestionIndex = Math.min(state.activeSuggestionIndex + 1, state.suggestions.length - 1);
                renderSuggestions();
            } else if (e.key === 'ArrowUp') {
                e.preventDefault();
                state.activeSuggestionIndex = Math.max(state.activeSuggestionIndex - 1, 0);
                renderSuggestions();
            } else if (e.key === 'Enter') {
                e.preventDefault();
                if (state.activeSuggestionIndex >= 0 && state.suggestions[state.activeSuggestionIndex]) {
                    addPlayer(state.suggestions[state.activeSuggestionIndex]);
                }
            } else if (e.key === 'Escape') {
                clearSuggestions();
            }
        });

        document.addEventListener('click', function (e) {
            if (e.target !== dom.playerSearchInput && !dom.suggestionList.contains(e.target)) {
                clearSuggestions();
            }
        });

        dom.retryButton.addEventListener('click', function () {
            state.hasError = false;
            if (initChart()) renderAll();
        });

        if (dom.advancedAnalysisBtn) {
            dom.advancedAnalysisBtn.addEventListener('click', function () {
                showToast('功能即将上线');
            });
        }
    }

    function renderSuggestions() {
        if (!state.query) { clearSuggestions(); return; }

        const params = new URLSearchParams({ q: state.query });
        if (state.filters.league !== '全部')   params.append('league',   state.filters.league);
        if (state.filters.season !== '全部')   params.append('season',   state.filters.season);
        if (state.filters.position !== '全部') params.append('position', state.filters.position);

        fetch('/api/players/search?' + params)
            .then((r) => r.json())
            .then(function (results) {
                const selectedStatIds = new Set(state.selectedPlayers.map((p) => p.statId));
                state.suggestions = results
                    .filter((r) => r.statId != null && !selectedStatIds.has(r.statId))
                    .map((r) => ({
                        playerId:   r.playerId,
                        playerName: r.playerName,
                        teamName:   r.teamName,
                        league:     r.league,
                        season:     r.season,
                        position:   r.position,
                        statId:     r.statId
                    }));

                if (state.suggestions.length === 0) { clearSuggestions(); return; }

                dom.suggestionList.innerHTML = state.suggestions.map(function (p, i) {
                    const cls = i === state.activeSuggestionIndex ? 'active' : '';
                    return '<li class="suggestion-item ' + cls + '" data-stat-id="' + p.statId + '">'
                        + '<div>' + p.playerName + '</div>'
                        + '<small>' + (p.teamName || '') + ' | ' + (p.league || '') + ' | ' + (p.season || '') + '</small>'
                        + '</li>';
                }).join('');

                dom.suggestionList.querySelectorAll('.suggestion-item').forEach(function (item) {
                    item.addEventListener('click', function () {
                        const sid = Number(item.getAttribute('data-stat-id'));
                        const candidate = state.suggestions.find((p) => p.statId === sid);
                        if (candidate) addPlayer(candidate);
                    });
                });

                dom.suggestionList.classList.remove('hidden');
            })
            .catch(function (err) {
                console.error('Search failed:', err);
                clearSuggestions();
            });
    }

    function clearSuggestions() {
        state.suggestions = [];
        state.activeSuggestionIndex = -1;
        dom.suggestionList.innerHTML = '';
        dom.suggestionList.classList.add('hidden');
    }

    function addPlayer(player) {
        if (state.selectedPlayers.length >= 3) {
            dom.warningText.textContent = '最多选择 3 人，请先移除后再添加。';
            return;
        }
        dom.warningText.textContent = '';
        state.selectedPlayers.push(player);
        state.query = '';
        dom.playerSearchInput.value = '';
        clearSuggestions();
        fetchCompareData();
    }

    function removePlayer(statId) {
        state.selectedPlayers = state.selectedPlayers.filter((p) => p.statId !== statId);
        dom.warningText.textContent = '';
        if (state.selectedPlayers.length === 0) {
            state.compareData = null;
            renderAll();
        } else {
            fetchCompareData();
        }
    }

    function fetchCompareData() {
        if (state.selectedPlayers.length === 0) {
            state.compareData = null;
            renderAll();
            return;
        }
        const ids = state.selectedPlayers.map((p) => p.statId).join(',');
        fetch('/api/players/compare?ids=' + ids)
            .then(function (r) {
                if (!r.ok) {
                    return r.text().then(function (body) {
                        throw new Error('HTTP ' + r.status + (body ? ': ' + body.slice(0, 200) : ''));
                    });
                }
                return r.json();
            })
            .then(function (data) {
                state.compareData = data;
                renderAll();
            })
            .catch(function (err) {
                console.error('Compare failed:', err);
                setError('数据加载失败，请重试。');
            });
    }

    function renderCards() {
        dom.selectedCount.textContent = '已选 ' + state.selectedPlayers.length + ' / 3';
        if (state.selectedPlayers.length === 0) { dom.playerCards.innerHTML = ''; return; }

        const apiPlayers = state.compareData ? state.compareData.players : [];

        dom.playerCards.innerHTML = state.selectedPlayers.map(function (sp, index) {
            const color = palette[index % palette.length];
            const ap = apiPlayers[index] || {};
            const name     = ap.playerName     || sp.playerName;
            const team     = ap.teamName       || sp.teamName    || '';
            const league   = ap.league         || sp.league      || '';
            const season   = ap.seasonLabel    || sp.season      || '';
            const position = ap.primaryPosition|| sp.position    || '';
            return '<article class="player-card">'
                + '<div class="color-bar" style="background:' + color + '"></div>'
                + '<div class="player-row">'
                + '<h3 class="player-name">' + name + '</h3>'
                + '<button class="remove-btn" data-stat-id="' + sp.statId + '" type="button">移除</button>'
                + '</div>'
                + '<p class="player-meta">' + team + '<br/>'
                + season + ' ' + league + '<br/>'
                + position + '</p>'
                + '</article>';
        }).join('');

        dom.playerCards.querySelectorAll('.remove-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                removePlayer(Number(btn.getAttribute('data-stat-id')));
            });
        });
    }

    function renderMetricList() {
        dom.metricList.innerHTML = '<h3>指标说明</h3>' + METRIC_CATALOG.map(function (m) {
            const color = GROUP_COLORS[m.group] || '#fff';
            return '<div class="metric-item">'
                + '<div class="metric-label" style="color:' + color + '">' + m.label + '</div>'
                + '<div class="metric-desc">' + m.group + '（0-100 百分位）</div>'
                + '</div>';
        }).join('');
    }

    function showState(text) { dom.stateBox.textContent = text; }

    function setError(message) {
        state.hasError = true;
        showState(message);
        dom.retryButton.classList.remove('hidden');
    }

    function clearError() {
        state.hasError = false;
        dom.retryButton.classList.add('hidden');
    }

    function showToast(message) {
        const toast = document.createElement('div');
        toast.className = 'toast';
        toast.textContent = message;
        dom.toastContainer.appendChild(toast);
        requestAnimationFrame(function () {
            requestAnimationFrame(function () {
                toast.classList.add('toast--visible');
            });
        });
        setTimeout(function () {
            toast.classList.remove('toast--visible');
            toast.addEventListener('transitionend', function () { toast.remove(); }, { once: true });
        }, 2500);
    }

    function renderRadar() {
        if (!state.chart) {
            if (!state.hasError) { showState('图表组件未就绪，请点击"重试"。'); dom.retryButton.classList.remove('hidden'); }
            return;
        }

        if (state.selectedPlayers.length === 0 || !state.compareData) {
            showState('空状态：请先选择至少 1 名球员。');
            state.chart.clear();
            state.chart.setOption({ backgroundColor: 'transparent', animation: false, series: [] }, true);
            return;
        }

        showState('加载中：正在刷新雷达图...');
        if (state.loadingTimer) window.clearTimeout(state.loadingTimer);

        state.loadingTimer = window.setTimeout(function () {
            try {
                const indicators = METRIC_CATALOG.map((m) => ({
                    name:  m.label,
                    max:   100,
                    color: GROUP_COLORS[m.group] || '#f3ffcf'
                }));

                const seriesData = state.compareData.players.map(function (player, idx) {
                    const values = METRIC_CATALOG.map(function (m) {
                        const metric = (player.metrics || []).find((x) => x.key === m.key);
                        return metric ? (metric.percentile || 0) : 0;
                    });
                    return {
                        value: values,
                        name: player.playerName + ' (' + (player.seasonLabel || '') + ')',
                        areaStyle: { color: hexToRgba(palette[idx], 0.2) },
                        lineStyle: { width: 2, color: palette[idx] },
                        itemStyle: { color: palette[idx] }
                    };
                });

                state.chart.setOption({
                    backgroundColor: 'transparent',
                    color: palette,
                    tooltip: {
                        trigger: 'item',
                        backgroundColor: 'rgba(15, 20, 32, 0.9)',
                        borderColor: '#232b3e',
                        textStyle: { color: '#fff', fontFamily: 'Outfit' }
                    },
                    legend: {
                        data: seriesData.map((d) => d.name),
                        bottom: 5,
                        textStyle: { color: '#8b99ae', fontFamily: 'Outfit', fontSize: 14 }
                    },
                    radar: {
                        indicator: indicators,
                        shape: 'polygon',
                        splitNumber: 5,
                        axisName: {
                            fontFamily: 'Outfit',
                            fontSize: 12,
                            fontWeight: 600
                        },
                        splitLine: {
                            lineStyle: {
                                color: [
                                    'rgba(204, 255, 0, 0.25)', 'rgba(204, 255, 0, 0.22)',
                                    'rgba(204, 255, 0, 0.19)', 'rgba(204, 255, 0, 0.16)',
                                    'rgba(204, 255, 0, 0.13)'
                                ]
                            }
                        },
                        splitArea: {
                            show: true,
                            areaStyle: { color: ['rgba(204, 255, 0, 0.03)', 'rgba(255, 255, 255, 0.015)'] }
                        },
                        axisLine: { lineStyle: { color: 'rgba(204, 255, 0, 0.22)' } }
                    },
                    series: [{
                        name: '能力对比 (Percentile Rank)',
                        type: 'radar',
                        data: seriesData,
                        symbol: 'circle',
                        symbolSize: 6,
                        itemStyle: { borderWidth: 2 },
                        lineStyle: { width: 3 },
                        areaStyle: { opacity: 0.15 }
                    }]
                }, true);

                showState('已加载：可点击图例临时隐藏/显示球员曲线。');
                clearError();
            } catch (err) {
                setError('数据异常：图表渲染失败，请重试。');
            }
        }, 350);
    }

    function renderAll() {
        if (state.hasError) return;
        renderCards();
        renderRadar();
    }

    function hexToRgba(hex, alpha) {
        const n = parseInt(hex.replace('#', ''), 16);
        return 'rgba(' + ((n >> 16) & 255) + ',' + ((n >> 8) & 255) + ',' + (n & 255) + ',' + alpha + ')';
    }

    // ── Soccer ball easter egg (unchanged logic) ──────────────────────────────
    function bindSoccerBall() {
        if (!dom.interactiveBall) return;
        const ball = dom.interactiveBall;
        const reducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        let targetX = 0, targetY = 0, currentX = 0, currentY = 0, rafId = null;

        function clamp(v, lo, hi) { return Math.max(lo, Math.min(hi, v)); }

        function renderFollow() {
            if (ball.classList.contains('kicked')) { rafId = null; return; }
            currentX += (targetX - currentX) * 0.16;
            currentY += (targetY - currentY) * 0.16;
            ball.style.transform = 'translate(' + currentX.toFixed(1) + 'px, ' + currentY.toFixed(1) + 'px) rotate(' + (currentX * -0.4).toFixed(1) + 'deg)';
            if (Math.abs(targetX - currentX) > 0.1 || Math.abs(targetY - currentY) > 0.1) {
                rafId = window.requestAnimationFrame(renderFollow);
            } else { rafId = null; }
        }

        function createKickBurst() {
            const rect = ball.getBoundingClientRect();
            const ox = rect.left + rect.width / 2, oy = rect.top + rect.height / 2;
            for (let i = 0; i < 14; i++) {
                const dot = document.createElement('span');
                dot.className = 'ball-trail';
                dot.style.left = ox + 'px'; dot.style.top = oy + 'px';
                const angle = (Math.PI * 2 * i) / 14;
                const dist = 45 + Math.random() * 80;
                dot.style.setProperty('--dx', Math.cos(angle) * dist + 'px');
                dot.style.setProperty('--dy', Math.sin(angle) * dist + 'px');
                document.body.appendChild(dot);
                dot.addEventListener('animationend', function () { dot.remove(); });
            }
        }

        if (!reducedMotion) {
            document.addEventListener('mousemove', function (e) {
                if (ball.classList.contains('kicked')) return;
                targetX = clamp((e.clientX / window.innerWidth - 0.5) * -18, -18, 18);
                targetY = clamp((e.clientY / window.innerHeight - 0.5) * -18, -18, 18);
                if (!rafId) rafId = window.requestAnimationFrame(renderFollow);
            });
        }

        function kickBall() {
            if (ball.classList.contains('kicked')) return;
            createKickBurst();
            ball.classList.add('kicked');
            window.setTimeout(function () {
                ball.classList.remove('kicked');
                targetX = 0; targetY = 0;
                if (!rafId) rafId = window.requestAnimationFrame(renderFollow);
            }, 900);
        }

        ball.addEventListener('click', kickBall);
        ball.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); kickBall(); }
        });
    }

    document.addEventListener('DOMContentLoaded', init);
    // ECharts 若走 CDN 回退，可能比 DOMContentLoaded 晚挂载到 window；窗口 load 后再补一次图表初始化
    window.addEventListener('load', function () {
        if (state.chart || !window.echarts) return;
        if (initChart()) renderAll();
    });
})();
