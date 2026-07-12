
-- users テーブルの構造を確認
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'users'
ORDER BY ordinal_position;

-- hatena テーブルの構造を確認（user_id があるか）
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'hatena'
ORDER BY ordinal_position;

-- 外部キー（users と hatena の紐づき）を確認
SELECT
  tc.constraint_name,
  kcu.column_name,
  ccu.table_name  AS references_table,
  ccu.column_name AS references_column
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
  ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
  ON ccu.constraint_name = tc.constraint_name
WHERE tc.table_name = 'hatena'
  AND tc.constraint_type = 'FOREIGN KEY';

-- インデックスを確認
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'hatena'
  AND indexname = 'idx_hatena_user_id';
