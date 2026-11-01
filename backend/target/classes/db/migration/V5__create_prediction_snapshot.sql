CREATE TABLE prediction_snapshot (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id                    BIGINT REFERENCES tracks(id) ON DELETE CASCADE,
    predicted_completion_date   DATE,
    pace_topics_per_week        NUMERIC(6,2),
    coverage_pct                NUMERIC(5,2),
    confidence                  VARCHAR(16) CHECK (confidence IN ('LOW', 'MEDIUM', 'HIGH')),
    ai_narrative                TEXT,
    ai_narrative_generated_at   TIMESTAMPTZ,
    computed_at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_prediction_snapshot_user_track ON prediction_snapshot(user_id, COALESCE(track_id, -1));
CREATE INDEX idx_prediction_snapshot_computed_at ON prediction_snapshot(user_id, computed_at);
