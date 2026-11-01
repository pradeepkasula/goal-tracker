# Goal Tracker

A personal daily goal tracker: configurable daily hour budgets (weekday/weekend/holiday) split
across learning Tracks (seeded with Java, System Design, AI), each with Topics and reference
links to read/practice, daily logging, dashboards/reports, and pace/ETA predictions (with an
optional local-AI narrative).

- **Backend**: `backend/` - Spring Boot, Jersey (JAX-RS) REST layer, Spring Data JPA, PostgreSQL,
  Flyway, Spring Security (JWT), Spring AI (Ollama by default).
- **Frontend**: `frontend/` - React + TypeScript + Vite, TanStack Query, Recharts, light/dark +
  multi-palette theming.

See [CHANGELOG.md](CHANGELOG.md) for the version history and versioning convention.

## Prerequisites

- JDK 17+ (the backend targets Java 17)
- Maven 3.9+
- PostgreSQL running locally, with a database and role created, e.g.:
  ```bash
  createdb goaltracker
  psql -c "CREATE ROLE goaltracker WITH LOGIN PASSWORD 'goaltracker';"
  psql -c "GRANT ALL PRIVILEGES ON DATABASE goaltracker TO goaltracker;"
  ```
  (Or just set `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` to point at whatever instance/role you already have.)
- Node.js 18+ and npm (for the frontend)
- Optional, for AI narrative predictions: [Ollama](https://ollama.com) running locally with a model pulled, e.g. `ollama pull llama3.1`. Everything works without it - predictions just fall back to stats-only.

## Backend

```bash
cd backend
mvn spring-boot:run
```

On first run, `DataSeeder` creates:
- User **`pkasula`** / password **`pkasula`** (change this once you've logged in once, or override `SEED_USERNAME`/`SEED_PASSWORD` env vars before the first run).
- Default day-type hour budgets (weekday 3h, weekend/holiday 5h).
- The 3 starter tracks (Java, System Design, AI) with ~12 topics each and reference links.
- Default per-track hour allocations for each day type.

Key environment variables (all optional, see `backend/src/main/resources/application.yml` for defaults):

| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Postgres connection |
| `JWT_SECRET` | HMAC signing secret - **set this to something real outside local dev** |
| `AI_ENABLED` | `true`/`false` - disable to skip AI calls entirely |
| `OLLAMA_BASE_URL`, `OLLAMA_MODEL` | Local Ollama endpoint/model for prediction narratives |
| `CORS_ALLOWED_ORIGINS` | Defaults to `http://localhost:5173` (the Vite dev server) |

### A note on this machine's Maven/npm setup

This machine's default `~/.m2/settings.xml` mirrors all Maven traffic to a corporate Artifactory
(`vitech.artifactorygold.repo`), and npm is configured similarly. While building this project in
this session's sandbox, that corporate proxy returned `403 Forbidden` for the public Spring Boot
artifacts (most likely because the sandbox isn't on the corporate VPN/IP allowlist) - direct
access to Maven Central worked fine from the same sandbox. Because of that, **the backend could
not be compiled or test-run in this session** - the code was written and manually reviewed, but
not built. On your own machine, if you're on the corporate VPN, `mvn spring-boot:run` may just
work as-is; if you hit the same 403, you'll need either VPN access, a working corporate proxy
entry for these artifacts, or a `-s` settings override pointed at Maven Central for local dev.
The same caveat applies to `npm install` for the frontend (same corporate registry).

## Frontend

```bash
cd frontend
cp .env.example .env   # adjust VITE_API_BASE_URL if the backend isn't on localhost:8080
npm install
npm run dev
```

Open the printed local URL (default `http://localhost:5173`) and sign in with the seeded user.

**Frontend build/run was not verified in this session** for the same registry-access reason as
the backend above - the source was written and reviewed but `npm install`/`npm run build` have
not actually been executed here.

## Tech notes

- The REST API is built on **Jersey (JAX-RS)**, not Spring MVC - resources live under `backend/src/main/java/com/goaltracker/resource/`, mounted at `/api` (see `config/JerseyConfig.java`).
- JWT auth is enforced by a JAX-RS filter (`security/JwtAuthFilter.java`) that runs inside Jersey's own chain, since Jersey - not Spring MVC - dispatches the resource methods.
- Schema is owned entirely by Flyway (`backend/src/main/resources/db/migration/`); `hibernate.ddl-auto` is `validate`, never `update`.
- A `daily_track_log` row's `planned_hours` is snapshotted from the current allocation the moment it's first created for that date, so editing your hour config later never rewrites past history.
- Predictions are cached in `prediction_snapshot` (default 6h TTL) so the stats engine and any AI call aren't re-run on every page view; use the "Refresh" button in the UI (or `POST .../refresh`) to force a recompute.
- Theming (light/dark + palette) is pure CSS variables keyed by `data-theme`/`data-mode` on `<html>` - see `frontend/src/styles/themes.css` and `frontend/src/theme/`.
