package com.southeast.scouting.player.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import com.southeast.scouting.player.dto.MetricDTO;
import com.southeast.scouting.player.dto.PlayerCompareItemDTO;
import com.southeast.scouting.player.dto.PlayerCompareResponseDTO;
import com.southeast.scouting.player.entity.PlayerSeasonStat;
import com.southeast.scouting.player.repository.PlayerSeasonStatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PlayerCompareService {

    private record MetricMeta(String key, String displayName, String group) {}

    private static final List<MetricMeta> METRIC_CATALOG = List.of(
        new MetricMeta("shotsPer90",                      "Shots /90",                  "attacking"),
        new MetricMeta("shotsOnTargetPct",                "Shots on Target %",          "attacking"),
        new MetricMeta("xaPer90",                         "xA /90",                     "attacking"),
        new MetricMeta("assistsPer90",                    "Assists /90",                "attacking"),
        new MetricMeta("xgPer90",                         "xG /90",                     "attacking"),
        new MetricMeta("nonPenGoalsPer90",                "Non-Pen Goals /90",          "attacking"),
        new MetricMeta("passesPer90",                     "Passes /90",                 "possession"),
        new MetricMeta("accuratePassesPct",               "Accurate Passes %",          "possession"),
        new MetricMeta("progressivePassesPer90",          "Progressive Passes /90",     "possession"),
        new MetricMeta("accurateProgressivePassesPct",    "Accurate Prog. Passes %",    "possession"),
        new MetricMeta("touchesInBoxPer90",               "Touches in Box /90",         "possession"),
        new MetricMeta("successfulDribblesPct",           "Successful Dribbles %",      "possession"),
        new MetricMeta("slidingTacklesPer90",             "Sliding Tackles /90",        "defending"),
        new MetricMeta("interceptionsPer90",              "Interceptions /90",          "defending"),
        new MetricMeta("shotsBlockedPer90",               "Shots Blocked /90",          "defending"),
        new MetricMeta("aerialDuelsWonPct",               "Aerial Duels Won %",         "defending"),
        new MetricMeta("successfulDefensiveActionsPer90", "Def. Actions /90",           "defending")
    );

    private final PlayerSeasonStatRepository statRepository;
    private final JsonMapper jsonMapper;

    public PlayerCompareService(PlayerSeasonStatRepository statRepository, JsonMapper jsonMapper) {
        this.statRepository = statRepository;
        this.jsonMapper = jsonMapper;
    }

    @Transactional(readOnly = true)
    public PlayerCompareResponseDTO compare(List<Long> statIds) {
        List<PlayerCompareItemDTO> items = statIds.stream()
            .map(this::buildItem)
            .toList();
        return new PlayerCompareResponseDTO(items);
    }

    private PlayerCompareItemDTO buildItem(Long statId) {
        PlayerSeasonStat stat = statRepository.findById(statId)
            .orElseThrow(() -> new IllegalArgumentException("stat not found: " + statId));

        Map<String, Object> rawMetrics;
        try {
            rawMetrics = jsonMapper.readValue(
                stat.getRawMetrics() != null ? stat.getRawMetrics() : "{}",
                new TypeReference<>() {}
            );
        } catch (Exception e) {
            rawMetrics = Map.of();
        }

        List<Object[]> pctRows = statRepository.fetchPercentilesByStatId(statId);
        Object[] pct = (pctRows != null && !pctRows.isEmpty()) ? pctRows.get(0) : new Object[17];

        List<MetricDTO> metrics = new ArrayList<>();
        for (int i = 0; i < METRIC_CATALOG.size(); i++) {
            MetricMeta meta = METRIC_CATALOG.get(i);
            Double value = toDouble(rawMetrics.get(meta.key()));
            Integer percentile = toIntegerPercentile(pct[i]);
            metrics.add(new MetricDTO(meta.key(), meta.displayName(), meta.group(), value, percentile));
        }

        PlayerCompareItemDTO item = new PlayerCompareItemDTO();
        item.setPlayerId(stat.getPlayer().getId());
        item.setPlayerName(stat.getPlayer().getDisplayName());
        item.setTeamName(stat.getTeam() != null ? stat.getTeam().getName() : null);
        item.setLeague(stat.getCompetition() != null ? stat.getCompetition().getName() : null);
        item.setSeasonLabel(stat.getSeasonLabel());
        item.setPrimaryPosition(stat.getPrimaryPosition());
        item.setPositionGroup(PositionNormalizer.normalize(stat.getPrimaryPosition()));
        item.setMetrics(metrics);
        return item;
    }

    private static Double toDouble(Object rawVal) {
        if (rawVal == null) {
            return null;
        }
        if (rawVal instanceof Number n) {
            return n.doubleValue();
        }
        if (rawVal instanceof String s && !s.isBlank()) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private static Integer toIntegerPercentile(Object cell) {
        return (cell instanceof Number n) ? n.intValue() : null;
    }
}
