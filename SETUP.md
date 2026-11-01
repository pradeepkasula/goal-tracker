# Setup Guide

Step-by-step instructions to get the Goal Tracker backend and frontend running locally. For a
feature overview see [README.md](README.md); for version history see [CHANGELOG.md](CHANGELOG.md).

## 1. Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 17+ | On this machine, use the `mvn17` wrapper (see below) so Maven runs on JDK 17 regardless of `JAVA_HOME`. |
| Maven | 3.9+ | |
| PostgreSQL | any recent version | Must be running locally before you start the backend. |
| Node.js | 18+ | 20.x confirmed working. |
| npm | 9+ | 11.x confirmed working. |
| Ollama (optional) | any | Only needed for AI-written prediction narratives; everything else works without it. |

## 2. Database setup

Create a dedicated role and database (only needs to be done once). Using `psql`:

```bash
psql -U postgres -c "CREATE ROLE goaltracker WITH LOGIN PASSWORD 'goaltracker';"
psql -U postgres -c "CREATE DATABASE goaltracker OWNER goaltracker;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE goaltracker TO goaltracker;"
```

If you already have a Postgres instance/role you'd rather use, skip this and just set the
`DB_URL`/`DB_USERNAME`/`DB_PASSWORD` environment variables (see step 3) to point at it instead.
The app expects a database reachable at `jdbc:postgresql://localhost:5432/goaltracker` by default.

Schema and seed data are created automatically on first backend startup (Flyway migrations, then
a one-time data seeder) - you don't need to create tables or insert data by hand.

## 3. Backend

```bash
cd backend
mvn17 spring-boot:run
```

(`mvn17` is a JDK-17-pinned copy of `mvn.bat` already on this machine, under
`apache-maven-3.9.15\bin\mvn17.bat`. Plain `mvn spring-boot:run` also works as long as
`JAVA_HOME` resolves to a JDK 17+ install.)

On success you'll see `Started GoalTrackerApplication` in the logs, followed by the seeder lines
creating the default user and the 3 starter tracks. The API is then live at
`http://localhost:8080/api`.

**First-run login**: username `pkasula`, password `pkasula` (change this once logged in, or set
the `SEED_USERNAME`/`SEED_PASSWORD` environment variables *before* the very first run to seed a
different account).

**Useful environment variables** (all optional - defaults live in
`backend/src/main/resources/application.yml`):

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/goaltracker` | Postgres connection URL |
| `DB_USERNAME` / `DB_PASSWORD` | `goaltracker` / `goaltracker` | Postgres credentials |
| `SERVER_PORT` | `8080` | Backend HTTP port |
| `JWT_SECRET` | a dev-only placeholder | **Set a real secret outside local dev** |
| `AI_ENABLED` | `true` | Set `false` to skip AI calls entirely (stats-only predictions) |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Local Ollama endpoint |
| `OLLAMA_MODEL` | `llama3.1` | Model used for prediction narratives (`ollama pull llama3.1` first) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Comma-separated list of allowed frontend origins |

To stop the backend: `Ctrl+C` in its terminal, or find and stop the Java process running
`com.goaltracker.GoalTrackerApplication`.

## 4. Frontend

```bash
cd frontend
npm install
npm run dev
```

Then open the printed URL - `http://localhost:5173` by default. Sign in with the same
`pkasula` / `pkasula` credentials.

The API base URL is read from `VITE_API_BASE_URL` (see `frontend/.env.example`); copy it to
`.env` and adjust if your backend isn't on `localhost:8080`:

```bash
cp .env.example .env
```

To stop the frontend: `Ctrl+C` in its terminal.

## 5. Verifying it's working

1. Backend: `curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"pkasula\",\"password\":\"pkasula\"}"` should return a JSON body with a `token`.
2. Frontend: opening `http://localhost:5173` should redirect to `/login`; after signing in you should land on `/dashboard` and see today's planned hours for Java/System Design/AI.
3. Predictions page: each track should show stats (coverage/pace); the AI narrative will say "AI insight unavailable right now - showing stats only" unless Ollama is running locally with the configured model pulled.

## 6. Resetting the database

To start over with a clean database (wipes all logged progress):

```bash
psql -U postgres -c "DROP DATABASE goaltracker;"
psql -U postgres -c "CREATE DATABASE goaltracker OWNER goaltracker;"
```

Then restart the backend - Flyway and the seeder will recreate everything from scratch.
