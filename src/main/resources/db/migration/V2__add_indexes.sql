CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_player_display_name_trgm
    ON player USING GIN (display_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_player_full_name_trgm
    ON player USING GIN (full_name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_player_season_stat_season
    ON player_season_stat (season_label);

CREATE INDEX IF NOT EXISTS idx_player_season_stat_team
    ON player_season_stat (team_id);

CREATE INDEX IF NOT EXISTS idx_player_season_stat_competition
    ON player_season_stat (competition_id);

CREATE INDEX IF NOT EXISTS idx_team_match_stat_team_date
    ON team_match_stat (team_id, match_date DESC);

CREATE INDEX IF NOT EXISTS idx_team_match_stat_competition_season
    ON team_match_stat (competition_id, season_label);

CREATE INDEX IF NOT EXISTS idx_player_metric_snapshot_player_season
    ON player_metric_snapshot (player_id, season_label);

CREATE INDEX IF NOT EXISTS idx_import_task_status_created
    ON import_task (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_import_task_error_task_row
    ON import_task_error (task_id, row_num);
