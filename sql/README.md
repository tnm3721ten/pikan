# SQL セットアップ

Pikan の DB スキーマは、アプリ起動時には自動作成されません（`spring.jpa.hibernate.ddl-auto=none`）。
新規環境では、以下の SQL を手動で実行してください。

## ファイル一覧

| ファイル | 用途 | 実行タイミング |
| --- | --- | --- |
| `setup_fresh.sql` | 新規 DB の初期セットアップ | 初回のみ（必須） |
| `verify_schema.sql` | テーブル構造の確認 | 任意 |

## セットアップ手順

### 1. データベースを作成する

```bash
createdb pikan
```

Windows で `createdb` が使えない場合は、psql 等から `CREATE DATABASE pikan;` を実行してください。

### 2. スキーマを作成する

```bash
psql -d pikan -f sql/setup_fresh.sql
```

### 3. 構造を確認する（任意）

```bash
psql -d pikan -f sql/verify_schema.sql
```

## 注意事項

- `setup_fresh.sql` は **空のデータベース向け** です
- 既存データがある DB に再実行すると、`CREATE TABLE IF NOT EXISTS` によりテーブル作成はスキップされますが、**既存スキーマとの不整合は自動では解消されません**
- スキーマ変更の履歴管理（Flyway 等）は未導入です
