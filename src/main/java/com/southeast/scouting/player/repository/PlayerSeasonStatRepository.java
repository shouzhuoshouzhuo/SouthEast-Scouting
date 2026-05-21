package com.southeast.scouting.player.repository;

import com.southeast.scouting.player.entity.PlayerSeasonStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerSeasonStatRepository extends JpaRepository<PlayerSeasonStat, Long> {

    @Query(value = """
        WITH ranked AS (
          SELECT id,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'shotsPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_shots_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'shotsOnTargetPct','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_shots_on_target_pct,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'xaPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_xa_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'assistsPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_assists_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'xgPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_xg_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'nonPenGoalsPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_non_pen_goals_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'passesPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_passes_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'accuratePassesPct','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_accurate_passes_pct,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'progressivePassesPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_progressive_passes_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'accurateProgressivePassesPct','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_accurate_progressive_passes_pct,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'touchesInBoxPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_touches_in_box_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'successfulDribblesPct','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_successful_dribbles_pct,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'slidingTacklesPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_sliding_tackles_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'interceptionsPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_interceptions_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'shotsBlockedPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_shots_blocked_per90,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'aerialDuelsWonPct','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_aerial_duels_won_pct,
            ROUND((PERCENT_RANK() OVER (
              PARTITION BY
                CASE
                  WHEN trim(split_part(primary_position,',',1)) = 'GK'                                    THEN 'GK'
                  WHEN trim(split_part(primary_position,',',1)) IN ('LB','RB','LWB','RWB','LB5','RB5')   THEN 'LB_RB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CB','LCB','RCB','LCB3','RCB3')      THEN 'CB'
                  WHEN trim(split_part(primary_position,',',1)) IN ('DMF','LDMF','RDMF')                 THEN 'DMF'
                  WHEN trim(split_part(primary_position,',',1)) IN ('CF','LW','RW','LWF','RWF')          THEN 'FW'
                  ELSE 'AMF'
                END
              ORDER BY CAST(NULLIF(raw_metrics->>'successfulDefensiveActionsPer90','') AS DOUBLE PRECISION) NULLS FIRST
            ) * 100)::integer) AS pct_successful_defensive_actions_per90
          FROM player_season_stat
        )
        SELECT
          pct_shots_per90, pct_shots_on_target_pct, pct_xa_per90, pct_assists_per90,
          pct_xg_per90, pct_non_pen_goals_per90, pct_passes_per90, pct_accurate_passes_pct,
          pct_progressive_passes_per90, pct_accurate_progressive_passes_pct,
          pct_touches_in_box_per90, pct_successful_dribbles_pct, pct_sliding_tackles_per90,
          pct_interceptions_per90, pct_shots_blocked_per90, pct_aerial_duels_won_pct,
          pct_successful_defensive_actions_per90
        FROM ranked WHERE id = :statId
        """, nativeQuery = true)
    List<Object[]> fetchPercentilesByStatId(@Param("statId") Long statId);

    @Query(value = "SELECT COUNT(*) FROM player_season_stat WHERE raw_metrics = CAST('{}' AS jsonb)", nativeQuery = true)
    long countWithEmptyObjectRawMetrics();
}
