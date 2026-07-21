# Pikan

## 概要

Pikanは、学習や業務の中で生まれた疑問を記録し、未解決・解決済みとして管理するWebアプリケーションです。

疑問を種類別に登録し、あとから回答を追記したり、解決状態を切り替えたりできます。

「何が分からないのか分からない」状態を防ぎ、疑問を可視化して学習や業務理解を進めやすくすることを目的としています。

## 作成背景

学習や業務の中で「分からない」と感じた疑問をそのまま放置してしまうと、後から何が分からなかったのかを思い出せなくなり、漠然とした不安や停滞感だけが残ることがありました。

前職の空港業務や現在の業務でも、分からないこと自体よりも、「何が分からないのか分からない」状態になることが、学習や業務理解を進めるうえで大きな障害になると感じていました。

メモアプリなどで疑問を残そうとしたこともありましたが、メモが散らばったり、他の情報に埋もれたりして、後から見返しにくいという課題がありました。そこで、日々の疑問を簡単に記録し、未解決・解決済みの状態で管理できるアプリを作りたいと考えました。

当初は新入社員や初学者向けの学習支援を意識していましたが、開発を進める中で、経験を積んだ人でも新しい分野に取り組むときは常に初心者であると感じました。

そのため、Pikanは「新人向け」に限らず、学習や仕事の中で生まれる疑問を整理し、前に進むためのアプリとして開発しています。

## 使用技術

| 区分 | 技術 | バージョン等 |
| --- | --- | --- |
| 言語 | Java | 25（`pom.xml`） |
| フレームワーク | Spring Boot | 4.0.5 |
| Web | Spring Web MVC | `spring-boot-starter-webmvc` |
| テンプレート | Thymeleaf | `spring-boot-starter-thymeleaf` |
| 認証 | Spring Security | `spring-boot-starter-security`（フォームログイン、BCrypt） |
| DB アクセス | Spring Data JPA / Hibernate | `spring-boot-starter-data-jpa` |
| DB | PostgreSQL | JDBC URL: `jdbc:postgresql://localhost:5432/pikan` |
| バリデーション | Jakarta Bean Validation | `spring-boot-starter-validation` |
| ユーティリティ | Lombok | `pom.xml` に定義 |
| ビルド | Maven Wrapper | `mvnw` / `mvnw.cmd` |

## 主な機能

### 認証・ユーザー管理

- **ユーザー登録**（`AuthController`, `RegisterService`）
  - URL: `GET /register`, `POST /register`
  - ユーザー名・パスワードをバリデーション後、`users` テーブルへ保存
  - パスワードは BCrypt でハッシュ化（`SecurityConfig.passwordEncoder()`）
- **ログイン**（`SecurityConfig`, `PikanUserDetailsService`）
  - URL: `GET /login`, `POST /login`（Spring Security フォームログイン）
  - 成功時: `/` へリダイレクト
  - 失敗時: `/login?error` へリダイレクト
- **ログアウト**（`SecurityConfig`）
  - URL: `POST /logout`
  - 成功時: `/login` へリダイレクト
- **未認証アクセス制御**
  - 認証が必要な URL へ未ログインでアクセスした場合、`/login?required` へリダイレクト

### はてな（疑問）管理

ログイン中ユーザーに紐づくデータのみ操作可能（`HatenaService`, `HatenaRepository`）。

- **一覧表示**（`GET /`）
  - ログイン中ユーザーのはてなのみ表示
  - ステータス（`OPEN` / `RESOLVED`）でタブ切り替え
  - 種別（`type`）・キーワード（`q`）で絞り込み・検索
  - キーワード検索対象: `content`, `answer`（部分一致・大文字小文字区別なし）
- **新規作成**
  - 種別選択: `GET /select-type`
  - 入力画面: `GET /create?type={HatenaType}`
  - 保存: `POST /save`
- **詳細表示**（`GET /detail/{id}`）
- **更新**（`POST /update`）
  - 本文・回答・解決済みフラグの更新
  - `OPEN` → `RESOLVED` への変更時のみ `resolved_at` を設定
- **削除**（`POST /delete`）

### はてなの種別・ステータス

- **種別（`HatenaType`）**: `WHAT`, `WHERE`, `WHO`, `WHEN`, `WHY`, `HOW`, `NORMAL`
- **ステータス（`HatenaStatus`）**: `OPEN`（未解決）, `RESOLVED`（解決済み）

## 画面一覧

| 画面名 | URL | HTTPメソッド | テンプレート | 認証 | 主なクラス |
| --- | --- | --- | --- | --- | --- |
| ログイン | `/login` | GET | `login.html` | 不要 | `AuthController`, `SecurityConfig` |
| ログイン送信 | `/login` | POST | ―（Spring Security） | 不要 | `SecurityConfig`, `PikanUserDetailsService` |
| ユーザー登録 | `/register` | GET | `register.html` | 不要 | `AuthController` |
| ユーザー登録送信 | `/register` | POST | `register.html`（エラー時） | 不要 | `AuthController`, `RegisterService` |
| ホーム（一覧） | `/` | GET | `index.html` | 必要 | `HatenaController` |
| 種別選択（5W1H） | `/select-type` | GET | `select-type.html` | 必要 | `HatenaController` |
| はてな作成 | `/create?type={type}` | GET | `create.html` | 必要 | `HatenaController` |
| はてな保存 | `/save` | POST | `create.html`（エラー時） | 必要 | `HatenaController`, `HatenaService` |
| はてな詳細 | `/detail/{id}` | GET | `detail.html` | 必要 | `HatenaController`, `HatenaService` |
| はてな更新 | `/update` | POST | `detail.html`（エラー時） | 必要 | `HatenaController`, `HatenaService` |
| はてな削除 | `/delete` | POST | ― | 必要 | `HatenaController`, `HatenaService` |
| ログアウト | `/logout` | POST | ― | 必要 | `SecurityConfig` |

### 補足

- 静的ファイル: `/css/*`（`src/main/resources/static/css/app.css`）は認証不要
- ホームのクエリパラメータ例:
  - `/?status=OPEN`
  - `/?status=RESOLVED`
  - `/?status=OPEN&type=NORMAL`
  - `/?status=OPEN&q=キーワード`

## 工夫した点

- 疑問を5W1Hと通常入力で分類できるようにし、あとから見返しやすい構成にしました。
- 未解決 / 解決済みの状態管理により、疑問を放置せず、解決状況を把握できるようにしました。
- content / answer を対象に検索できるようにし、過去の疑問や回答を探しやすくしました。
- ログイン中ユーザーに紐づくデータのみ表示・更新・削除できるようにし、他ユーザーのデータを操作できないようにしました。
- 入力値に対してバリデーションを設定し、空白のみ・文字数超過などの不正なデータを保存しないようにしました。
- 手動テストケースを作成し、正常系・異常系・境界値・認証認可・DB確認まで確認しました。

## テスト

以下の観点で手動テストを実施しました。

- ユーザー登録
- ログイン / ログアウト
- ホーム画面
- 種別選択画面
- はてな作成
- はてな詳細表示
- はてな更新
- はてな削除
- 検索・絞り込み
- 認証・認可
- DB保存内容の確認

正常系だけでなく、入力エラー、境界値、未ログイン時のアクセス制御、他ユーザーのデータ操作防止、DB保存内容の確認も実施しました。

手動テストでは、画面操作だけでなく、DevToolsを用いたPOST送信やDB確認も行い、認証・認可やデータ更新の挙動を確認しました。

## 今後の改善予定

- AWS等を利用したアプリの公開
- READMEへの画面キャプチャ追加
- 検索機能の改善
- 過去の疑問との重複表示
- 疑問の傾向を振り返る機能
- 学習支援につながる振り返り機能
- 自動テストの追加

## 起動方法

### 前提条件

1. **Java 25** がインストールされていること
2. **PostgreSQL** が起動していること
3. データベース **`pikan`** が作成されていること
4. テーブルが作成されていること（`spring.jpa.hibernate.ddl-auto=none` のため、自動作成されない）

### DB セットアップ

`sql/` 配下の SQL を手動で実行してください（`spring.sql.init.mode=never` のため、アプリ起動時には実行されません）。

詳細は [sql/README.md](sql/README.md) を参照してください。

**新規環境の最短手順:**

1. データベース `pikan` を作成する
2. `sql/setup_fresh.sql` を実行する
3. 必要に応じて `sql/verify_schema.sql` で構造を確認する

### DB 接続設定

DB接続の共通設定は `src/main/resources/application.properties` にあります。
DBパスワードなど、環境ごとの設定は Git 管理外の `config/application-local.properties` に書きます。

初回のみ、以下を実行してください。

```bash
# Windows
copy config\application-local.properties.example config\application-local.properties

# Linux / macOS
cp config/application-local.properties.example config/application-local.properties
```

コピー後、`config/application-local.properties` を開き、PostgreSQL のパスワードを設定してください。

```properties
spring.datasource.password=YOUR_LOCAL_DB_PASSWORD
```

| ファイル | 役割 | Git管理 |
| --- | --- | --- |
| `src/main/resources/application.properties` | 共通設定 | する |
| `config/application-local.properties.example` | 設定例 | する |
| `config/application-local.properties` | ローカル用（パスワード等） | しない |

### アプリケーション起動

`application.properties` で `local` プロファイルが有効になるため、次のコマンドで起動できます。

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

### アクセス URL

- `server.port` の設定は `application.properties` にないため、Spring Boot のデフォルト **8080** を使用
- 起動後: **http://localhost:8080/login**

## ディレクトリ構成

```
pikan/
├── config/                  # ローカル専用設定（example のみ Git 管理）
│   ├── application-local.properties.example
│   └── application-local.properties  # Git 管理外
├── mvnw / mvnw.cmd          # Maven Wrapper
├── pom.xml                  # 依存関係・ビルド設定
├── sql/                     # DB セットアップ SQL
│   ├── README.md
│   ├── setup_fresh.sql
│   └── verify_schema.sql
└── src/
    ├── main/
    │   ├── java/com/example/pikan/
    │   │   ├── PikanApplication.java       # 起動クラス
    │   │   ├── config/
    │   │   │   └── SecurityConfig.java     # 認証・認可設定
    │   │   ├── controller/
    │   │   │   ├── AuthController.java     # ログイン画面・ユーザー登録
    │   │   │   └── HatenaController.java   # はてな CRUD
    │   │   ├── entity/
    │   │   │   ├── Hatena.java             # はてなエンティティ
    │   │   │   └── User.java               # ユーザーエンティティ
    │   │   ├── enumtype/
    │   │   │   ├── HatenaStatus.java
    │   │   │   └── HatenaType.java
    │   │   ├── form/
    │   │   │   ├── HatenaCreateForm.java
    │   │   │   ├── HatenaUpdateForm.java
    │   │   │   ├── RegisterForm.java
    │   │   │   ├── RegisterFormGroups.java
    │   │   │   └── RegisterFormValidator.java
    │   │   ├── repository/
    │   │   │   ├── HatenaRepository.java
    │   │   │   └── UserRepository.java
    │   │   ├── service/
    │   │   │   ├── HatenaService.java
    │   │   │   ├── PikanUserDetailsService.java
    │   │   │   └── RegisterService.java
    │   │   └── util/
    │   │       └── DateTimeFormats.java
    │   └── resources/
    │       ├── application.properties
    │       ├── static/css/app.css
    │       └── templates/                  # Thymeleaf テンプレート
    │           ├── index.html
    │           ├── login.html
    │           ├── register.html
    │           ├── select-type.html
    │           ├── create.html
    │           └── detail.html
    └── test/
        └── java/                           # テスト用ディレクトリ（現状ソースなし）
```

## 作者

GitHub: [tnm3721ten](https://github.com/tnm3721ten)
