CREATE TABLE daily_track_log (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id        BIGINT NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    log_date        DATE NOT NULL,
    day_type        VARCHAR(16) NOT NULL CHECK (day_type IN ('WEEKDAY', 'WEEKEND', 'HOLIDAY')),
    planned_hours   NUMERIC(4,2) NOT NULL,
    actual_hours    NUMERIC(4,2) NOT NULL DEFAULT 0,
    status          VARCHAR(16) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PARTIAL', 'MET', 'EXCEEDED', 'MISSED')),
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_daily_track_log UNIQUE (user_id, track_id, log_date)
);

CREATE INDEX idx_daily_track_log_user_date ON daily_track_log(user_id, log_date);
CREATE INDEX idx_daily_track_log_user_track ON daily_track_log(user_id, track_id);

CREATE TABLE topic_log (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic_id        BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    log_date        DATE NOT NULL,
    status          VARCHAR(16) NOT NULL CHECK (status IN ('STARTED', 'IN_PROGRESS', 'PRACTICED', 'COMPLETED')),
    minutes_spent   INTEGER NOT NULL DEFAULT 0,
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_topic_log_user_topic_date ON topic_log(user_id, topic_id, log_date);

CREATE TABLE topic_progress (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic_id                BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    status                  VARCHAR(16) NOT NULL DEFAULT 'NOT_STARTED' CHECK (status IN ('NOT_STARTED', 'STARTED', 'IN_PROGRESS', 'PRACTICED', 'COMPLETED')),
    times_practiced         INTEGER NOT NULL DEFAULT 0,
    total_minutes_spent     INTEGER NOT NULL DEFAULT 0,
    last_practiced_at       TIMESTAMPTZ,
    completed_at            TIMESTAMPTZ,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_topic_progress UNIQUE (user_id, topic_id)
);
