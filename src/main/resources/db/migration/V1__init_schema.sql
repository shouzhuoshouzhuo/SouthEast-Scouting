CREATE TABLE IF NOT EXISTS team (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    normalized_name VARCHAR(128) NOT NULL,
    logo_url TEXT,
    country VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_team_normalized_name UNIQUE (normalized_name)
);

CREATE TABLE IF NOT EXISTS player (
    id BIGSERIAL PRIMARY KEY,
    wyscout_id BIGINT,
    display_name VARCHAR(128) NOT NULL,
    full_name VARCHAR(128),
    normalized_name VARCHAR(128) NOT NULL,
    birth_date DATE,
    age SMALLINT,
    birth_country VARCHAR(64),
    passport_country_raw TEXT,
    foot VARCHAR(16),
    height_cm SMALLINT,
    weight_kg SMALLINT,
    market_value_eur BIGINT,
    contract_expires DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_player_wyscout_id UNIQUE (wyscout_id)
);

CREATE TABLE IF NOT EXISTS competition (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    season_label VARCHAR(32) NOT NULL,
    country VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_competition_name_season UNIQUE (name, season_label)
);

CREATE TABLE IF NOT EXISTS player_season_stat (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES player(id),
    team_id BIGINT REFERENCES team(id),
    competition_id BIGINT REFERENCES competition(id),
    season_label VARCHAR(32) NOT NULL,
    primary_position VARCHAR(64),
    secondary_position VARCHAR(64),
    third_position VARCHAR(64),
    matches_played INTEGER,
    minutes_played INTEGER,
    goals NUMERIC(10, 2),
    assists NUMERIC(10, 2),
    xg NUMERIC(10, 4),
    xa NUMERIC(10, 4),
    on_loan BOOLEAN,
    raw_metrics JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_file VARCHAR(255),
    source_row_num INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_player_season_stat UNIQUE (player_id, competition_id, season_label, team_id)
);

CREATE TABLE IF NOT EXISTS team_match_stat (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL REFERENCES team(id),
    competition_id BIGINT REFERENCES competition(id),
    season_label VARCHAR(32) NOT NULL,
    match_date DATE NOT NULL,
    match_label VARCHAR(255),
    goals INTEGER,
    goals_conceded INTEGER,
    xg NUMERIC(10, 4),
    xga NUMERIC(10, 4),
    possession NUMERIC(8, 4),
    shots INTEGER,
    shots_faced INTEGER,
    ppda NUMERIC(10, 4),
    raw_metrics JSONB NOT NULL DEFAULT '{}'::jsonb,
    source_file VARCHAR(255),
    source_row_num INTEGER,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_team_match_stat UNIQUE (team_id, season_label, match_date, match_label)
);

CREATE TABLE IF NOT EXISTS player_metric_snapshot (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT NOT NULL REFERENCES player(id),
    season_label VARCHAR(32) NOT NULL,
    metric_set_version VARCHAR(32) NOT NULL DEFAULT 'v1',
    score_attack NUMERIC(10, 4),
    score_defense NUMERIC(10, 4),
    score_possession NUMERIC(10, 4),
    score_overall NUMERIC(10, 4),
    raw_scores JSONB NOT NULL DEFAULT '{}'::jsonb,
    computed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_player_metric_snapshot UNIQUE (player_id, season_label, metric_set_version)
);

CREATE TABLE IF NOT EXISTS import_task (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_hash_sha256 CHAR(64) NOT NULL,
    import_type VARCHAR(32) NOT NULL,
    season_label VARCHAR(32),
    status VARCHAR(16) NOT NULL,
    total_rows INTEGER NOT NULL DEFAULT 0,
    success_rows INTEGER NOT NULL DEFAULT 0,
    failed_rows INTEGER NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_import_task_file_hash UNIQUE (file_hash_sha256)
);

CREATE TABLE IF NOT EXISTS import_task_error (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES import_task(id) ON DELETE CASCADE,
    row_num INTEGER NOT NULL,
    field_name VARCHAR(128),
    rejected_value TEXT,
    error_reason TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
