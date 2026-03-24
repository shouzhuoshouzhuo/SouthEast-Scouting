(function () {
    const metricCatalog = [
        { key: "successfulTakeOn", label: "Successful Take-On %", desc: "过人成功率百分位", unit: "%" },
        { key: "tklInt", label: "Tkl+Int", desc: "抢断+拦截综合能力", unit: "pct" },
        { key: "tackles", label: "Tackles", desc: "抢断总量能力", unit: "pct" },
        { key: "tacklesWon", label: "Tackles Won", desc: "抢断成功能力", unit: "pct" },
        { key: "ballRecoveries", label: "Ball Recoveries", desc: "球权回收能力", unit: "pct" },
        { key: "tacklesDef3rd", label: "Tackles (Def 3rd)", desc: "后场三区抢断", unit: "pct" },
        { key: "tacklesMid3rd", label: "Tackles (Mid 3rd)", desc: "中场三区抢断", unit: "pct" },
        { key: "tacklesAtt3rd", label: "Tackles (Att 3rd)", desc: "前场三区抢断", unit: "pct" },
        { key: "tackledDuringTakeOn", label: "Tackled During Take-On %", desc: "被断球率（越低越好，已正向化）", unit: "%" },
        { key: "interceptions", label: "Interceptions", desc: "拦截能力", unit: "pct" },
        { key: "aerialWinPct", label: "% of Aerials Won", desc: "争顶成功率", unit: "%" },
        { key: "aerialsWon", label: "Aerials Won", desc: "争顶成功总量", unit: "pct" }
    ];

    const players = [
        {
            playerId: "ndidi-2022-pl",
            playerName: "Wilfred Ndidi",
            teamName: "Leicester City",
            league: "Premier League",
            season: "2022-2023",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 22, tklInt: 45, tackles: 30, tacklesWon: 43,
                ballRecoveries: 58, tacklesDef3rd: 95, tacklesMid3rd: 67, tacklesAtt3rd: 39,
                tackledDuringTakeOn: 66, interceptions: 69, aerialWinPct: 72, aerialsWon: 77
            }
        },
        {
            playerId: "ndidi-2023-champ",
            playerName: "Wilfred Ndidi",
            teamName: "Leicester City",
            league: "Championship",
            season: "2023-2024",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 78, tklInt: 93, tackles: 89, tacklesWon: 95,
                ballRecoveries: 88, tacklesDef3rd: 99, tacklesMid3rd: 82, tacklesAtt3rd: 57,
                tackledDuringTakeOn: 25, interceptions: 98, aerialWinPct: 60, aerialsWon: 79
            }
        },
        {
            playerId: "ndidi-2024-pl",
            playerName: "Wilfred Ndidi",
            teamName: "Leicester City",
            league: "Premier League",
            season: "2024-2025",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 36, tklInt: 79, tackles: 82, tacklesWon: 86,
                ballRecoveries: 52, tacklesDef3rd: 96, tacklesMid3rd: 80, tacklesAtt3rd: 56,
                tackledDuringTakeOn: 18, interceptions: 72, aerialWinPct: 54, aerialsWon: 92
            }
        },
        {
            playerId: "rice-2024-pl",
            playerName: "Declan Rice",
            teamName: "Arsenal",
            league: "Premier League",
            season: "2024-2025",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 62, tklInt: 87, tackles: 73, tacklesWon: 77,
                ballRecoveries: 85, tacklesDef3rd: 74, tacklesMid3rd: 88, tacklesAtt3rd: 44,
                tackledDuringTakeOn: 49, interceptions: 81, aerialWinPct: 68, aerialsWon: 76
            }
        },
        {
            playerId: "rodri-2024-pl",
            playerName: "Rodri",
            teamName: "Manchester City",
            league: "Premier League",
            season: "2024-2025",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 69, tklInt: 84, tackles: 59, tacklesWon: 64,
                ballRecoveries: 94, tacklesDef3rd: 61, tacklesMid3rd: 79, tacklesAtt3rd: 42,
                tackledDuringTakeOn: 57, interceptions: 92, aerialWinPct: 71, aerialsWon: 83
            }
        },
        {
            playerId: "kimmich-2024-bundesliga",
            playerName: "Joshua Kimmich",
            teamName: "Bayern Munich",
            league: "Bundesliga",
            season: "2024-2025",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 66, tklInt: 73, tackles: 69, tacklesWon: 71,
                ballRecoveries: 76, tacklesDef3rd: 57, tacklesMid3rd: 75, tacklesAtt3rd: 55,
                tackledDuringTakeOn: 54, interceptions: 70, aerialWinPct: 48, aerialsWon: 45
            }
        },
        {
            playerId: "valverde-2024-laliga",
            playerName: "Federico Valverde",
            teamName: "Real Madrid",
            league: "La Liga",
            season: "2024-2025",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 74, tklInt: 78, tackles: 67, tacklesWon: 72,
                ballRecoveries: 79, tacklesDef3rd: 54, tacklesMid3rd: 82, tacklesAtt3rd: 61,
                tackledDuringTakeOn: 58, interceptions: 68, aerialWinPct: 52, aerialsWon: 58
            }
        },
        {
            playerId: "barella-2024-seriea",
            playerName: "Nicolo Barella",
            teamName: "Inter",
            league: "Serie A",
            season: "2024-2025",
            position: "MF",
            compareContext: "Percentile rank vs. MF",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 72, tklInt: 70, tackles: 66, tacklesWon: 68,
                ballRecoveries: 74, tacklesDef3rd: 49, tacklesMid3rd: 79, tacklesAtt3rd: 63,
                tackledDuringTakeOn: 62, interceptions: 65, aerialWinPct: 41, aerialsWon: 40
            }
        },
        {
            playerId: "wirtz-2024-bundesliga",
            playerName: "Florian Wirtz",
            teamName: "Bayer Leverkusen",
            league: "Bundesliga",
            season: "2024-2025",
            position: "FW",
            compareContext: "Percentile rank vs. FW",
            updatedAt: "2026-03-24",
            metrics: {
                successfulTakeOn: 92, tklInt: 45, tackles: 33, tacklesWon: 37,
                ballRecoveries: 44, tacklesDef3rd: 16, tacklesMid3rd: 47, tacklesAtt3rd: 70,
                tackledDuringTakeOn: 71, interceptions: 42, aerialWinPct: 34, aerialsWon: 31
            }
        }
    ];

    window.ScoutingMockData = {
        leagues: ["全部", "Premier League", "La Liga", "Bundesliga", "Serie A", "Ligue 1", "Championship"],
        seasons: ["全部", "2022-2023", "2023-2024", "2024-2025"],
        positions: ["全部", "FW", "MF", "DF", "GK"],
        metricCatalog,
        players
    };
})();

