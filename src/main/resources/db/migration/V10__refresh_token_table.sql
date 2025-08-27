CREATE TABLE IF NOT EXISTS refresh_token (
  id           BIGSERIAL PRIMARY KEY,
  token        TEXT NOT NULL UNIQUE,
  user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  expires_at   TIMESTAMP NOT NULL,
  revoked      BOOLEAN NOT NULL DEFAULT FALSE,
  created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_token(user_id);
