CREATE TABLE day_type_hour_config (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_type        VARCHAR(16) NOT NULL CHECK (day_type IN ('WEEKDAY', 'WEEKEND', 'HOLIDAY')),
    total_hours     NUMERIC(4,2) NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_day_type_hour_config UNIQUE (user_id, day_type)
);

CREATE TABLE track_hour_allocation (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id            BIGINT NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    day_type            VARCHAR(16) NOT NULL CHECK (day_type IN ('WEEKDAY', 'WEEKEND', 'HOLIDAY')),
    allocated_hours     NUMERIC(4,2) NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_track_hour_allocation UNIQUE (user_id, track_id, day_type)
);

CREATE INDEX idx_track_hour_allocation_user_daytype ON track_hour_allocation(user_id, day_type);

CREATE TABLE holidays (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    holiday_date    DATE NOT NULL,
    label           VARCHAR(128),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_holidays_user_date UNIQUE (user_id, holiday_date)
);
