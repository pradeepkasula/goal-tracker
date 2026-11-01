CREATE TABLE tracks (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(128) NOT NULL,
    description     TEXT,
    color_hex       VARCHAR(7),
    sort_order      INTEGER NOT NULL DEFAULT 0,
    archived        BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_tracks_user_name UNIQUE (user_id, name)
);

CREATE TABLE topics (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    track_id            BIGINT NOT NULL REFERENCES tracks(id) ON DELETE CASCADE,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    sort_order          INTEGER NOT NULL DEFAULT 0,
    estimated_minutes   INTEGER,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_topics_track_title UNIQUE (track_id, title)
);

CREATE INDEX idx_topics_track_id ON topics(track_id);
CREATE INDEX idx_topics_user_id ON topics(user_id);

CREATE TABLE topic_references (
    id              BIGSERIAL PRIMARY KEY,
    topic_id        BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reference_type  VARCHAR(32) NOT NULL CHECK (reference_type IN ('ARTICLE', 'VIDEO', 'DOC', 'COURSE', 'BOOK', 'OTHER')),
    title           VARCHAR(255) NOT NULL,
    url             VARCHAR(2048) NOT NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_topic_references_topic_id ON topic_references(topic_id);
