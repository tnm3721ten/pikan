

-- 1-1. users テーブルを作成する
CREATE TABLE IF NOT EXISTS users (
  id         BIGSERIAL PRIMARY KEY,
  username   VARCHAR(20)  NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 1-2. 開発中の既存 hatena データを削除する
DELETE FROM hatena;

-- （例）id が 5 の行だけ消すなら
--DELETE FROM hatena WHERE id = 5;
--（例）status が 'RESOLVED' の行だけ消すなら
--DELETE FROM hatena WHERE status = 'RESOLVED';
--（例）type が 'WHY' の行だけ消すなら
--DELETE FROM hatena WHERE type = 'WHY';
--（例）content に 'test' が含まれる行だけ消すなら（部分一致）（「前後に何文字あってもいい」というワイルドカード）
--DELETE FROM hatena WHERE content LIKE '%test%';
--（例）created_at が '2026-01-01 00:00:00' から '2026-01-01 23:59:59' の間の行だけ消すなら
--DELETE FROM hatena WHERE created_at BETWEEN '2026-01-01 00:00:00' AND '2026-01-01 23:59:59';

-- 1-3. hatena テーブルに user_id を追加する
--      （データを空にしたあとなので、NOT NULL を付けられる）
ALTER TABLE hatena
  ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id);

CREATE INDEX idx_hatena_user_id ON hatena(user_id);
