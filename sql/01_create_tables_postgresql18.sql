-- pikan DB (PostgreSQL 18)
-- Design source: pikan_DB設計書.pdf

CREATE TABLE IF NOT EXISTS hatena (
  -- データの量が多くなってもいいように、BIGSERIALを使用している
  id BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  --20文字と制限することで、DBは「この項目はこれくらいの容量を確保すればいいな」と予測できる。
  --CHECK句は、リストにある言葉以外だめというルール。ＤＢを守るための念押し「CHECK (type IN (...))」
  type VARCHAR(20) NOT NULL CHECK (type IN ('WHY', 'HOW', 'WHAT', 'WHEN', 'WHICH', 'NORMAL')),
  --長さが予測できないのでTEXTを使用。
  content TEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'RESOLVED')),
  answer TEXT,
  resolved_at TIMESTAMP,
  --絶対に守らなければならない規則。一応書いている。
  CONSTRAINT chk_hatena_resolved_at_status
    CHECK (
      --ステータスが「未解決（OPEN）」なら、解決日時（resolved_at）は「空っぽ（NULL）」であること
      (status = 'OPEN' AND resolved_at IS NULL)
      OR
      --ステータスが「解決済み（RESOLVED）」なら、解決日時（resolved_at）は「空っぽではない（NOT NULL）」
      (status = 'RESOLVED' AND resolved_at IS NOT NULL)
    )
);

--CREATE INDEX インデックス名 ON テーブル名 (カラム名);
CREATE INDEX IF NOT EXISTS idx_hatena_status ON hatena(status);
CREATE INDEX IF NOT EXISTS idx_hatena_type ON hatena(type);
CREATE INDEX IF NOT EXISTS idx_hatena_created_at ON hatena(created_at);


--★基礎知識
--status (名前) → VARCHAR(20) (型) → NOT NULL (制約A) → DEFAULT 'OPEN' (制約B)
