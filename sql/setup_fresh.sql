-- pikan DB 新規セットアップ用（PostgreSQL）
-- 空のデータベース pikan に対して1回実行してください。

CREATE TABLE IF NOT EXISTS users (
  id         BIGSERIAL PRIMARY KEY,
  username   VARCHAR(20)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hatena (
  id          BIGSERIAL PRIMARY KEY,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  type        VARCHAR(20) NOT NULL CHECK (type IN ('WHAT', 'WHERE', 'WHO', 'WHEN', 'WHY', 'HOW', 'NORMAL')),
  content     TEXT NOT NULL,
  status      VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'RESOLVED')),
  answer      TEXT,
  resolved_at TIMESTAMP,
  user_id     BIGINT NOT NULL REFERENCES users(id),
  CONSTRAINT chk_hatena_resolved_at_status
    CHECK (
      (status = 'OPEN' AND resolved_at IS NULL)
      OR
      (status = 'RESOLVED' AND resolved_at IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_hatena_status ON hatena(status);
CREATE INDEX IF NOT EXISTS idx_hatena_type ON hatena(type);
CREATE INDEX IF NOT EXISTS idx_hatena_created_at ON hatena(created_at);
CREATE INDEX IF NOT EXISTS idx_hatena_user_id ON hatena(user_id);
