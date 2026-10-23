# Music Genre Encyclopedia — Backend (Spring Boot REST API)

The **API Application** container from the C4 diagrams: Spring Boot 3 + Spring
Data JPA + Spring Security (JWT) + PostgreSQL. Java 21.

## Modules (match the C4 component/code diagrams)

| Package                  | Responsibility                                            |
|--------------------------|----------------------------------------------------------|
| `auth`                   | Login/register, JWT, `User` + `UserRole` (USER/ADMIN)    |
| `genre`                  | Genre CRUD (`Genre`, `GenreController`, `GenreService`)   |
| `article`               | Articles + community revisions (PENDING/APPROVED/REJECTED)|
| `moderation`             | Approve/reject revisions, `NotificationPort`              |
| `complaint`              | File + resolve complaints (OPEN/RESOLVED)                 |
| `common`                 | Error handling, JSON helpers                              |
| `config`                 | Security, CORS, data seeding                              |

---

## 1. What you need to connect the database

### a) Install PostgreSQL
Install **PostgreSQL 14 or newer** (https://www.postgresql.org/download/windows/).
During setup remember the **superuser (`postgres`) password**.

### b) Create the database and user
Run the provided script **`db/init.sql`** once as the `postgres` user
(`psql -U postgres -f db/init.sql`), or open **psql** / pgAdmin → Query Tool and run:

```sql
CREATE DATABASE music_encyclopedia;
CREATE USER encyclopedia WITH PASSWORD 'encyclopedia';
GRANT ALL PRIVILEGES ON DATABASE music_encyclopedia TO encyclopedia;

-- PostgreSQL 15+ also needs schema rights:
\c music_encyclopedia
GRANT ALL ON SCHEMA public TO encyclopedia;
```

> Tables are **created automatically** on first startup (`ddl-auto: update`),
> and 6 sample genres + `admin`/`demo` users are seeded.

### c) Point the app at your database
Defaults (in `src/main/resources/application.yml`) already match the user above:

| Setting      | Default                                             | Env var        |
|--------------|-----------------------------------------------------|----------------|
| URL          | `jdbc:postgresql://localhost:5432/music_encyclopedia` | `DB_URL`     |
| Username     | `encyclopedia`                                      | `DB_USERNAME`  |
| Password     | `encyclopedia`                                      | `DB_PASSWORD`  |
| JWT secret   | (dev default — **change in prod**)                  | `JWT_SECRET`   |
| CORS origin  | `http://localhost:5173`                             | `CORS_ORIGINS` |

If you used different credentials, override them, e.g. in PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/music_encyclopedia"
$env:DB_USERNAME="encyclopedia"
$env:DB_PASSWORD="your-password"
```

`JWT_SECRET` must be a **Base64-encoded key of at least 256 bits** (32+ bytes).
Generate one: `openssl rand -base64 48`.

---

## 2. Run

You do **not** need Maven installed — use the wrapper (it downloads Maven on first run):

```powershell
cd Music-Genre-Encyclopedia\backend
.\mvnw.cmd spring-boot:run
```

API starts on **http://localhost:8080**. (Build a jar with `.\mvnw.cmd clean package`.)

## 3. Smoke-test the API

```powershell
# list genres (public)
curl http://localhost:8080/api/genres

# log in as the seeded admin
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"username":"admin","password":"admin"}'
# -> { "token": "...", "username": "admin", "role": "ADMIN" }
```

## 4. Endpoints

| Method | Path                                       | Access        |
|--------|--------------------------------------------|---------------|
| POST   | `/api/auth/register`                       | public        |
| POST   | `/api/auth/login`                          | public        |
| GET    | `/api/genres`                              | public        |
| GET    | `/api/genres/{slug}`                       | public        |
| POST   | `/api/genres`                              | ADMIN         |
| PUT    | `/api/genres/{id}`                         | ADMIN         |
| DELETE | `/api/genres/{id}`                         | ADMIN         |
| GET    | `/api/articles/{articleId}`                | public        |
| GET    | `/api/articles/{articleId}/revisions`      | public        |
| POST   | `/api/articles/{articleId}/revisions`      | USER (logged) |
| GET    | `/api/moderation/revisions`                | ADMIN         |
| POST   | `/api/moderation/revisions/{id}/approve`   | ADMIN         |
| POST   | `/api/moderation/revisions/{id}/reject`    | ADMIN         |
| POST   | `/api/complaints`                          | USER (logged) |
| GET    | `/api/complaints`                          | ADMIN         |
| POST   | `/api/complaints/{id}/resolve`             | ADMIN         |

Send the JWT as `Authorization: Bearer <token>` on protected calls.

## 5. Notes
- `ddl-auto: update` is for easy dev. For production switch to `validate` and use
  Flyway/Liquibase migrations.
- A test-scoped H2 smoke test (`ContextAndFlowTest`) verifies wiring and the
  propose→approve workflow without needing PostgreSQL: `.\mvnw.cmd test`.
