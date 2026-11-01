# Changelog

Version numbers are `MAJOR.MINOR` (no patch digit for this personal project): `1.0` is the
initial release, `1.1`/`1.2`/... each mark one subsequent improvement. Every change that's more
than a typo fix gets its own entry here, added with the commit that ships it. The backend's
`pom.xml` version and the frontend's `package.json` version track the same number (as `x.y.0`).

## 1.0 — 2026-10-31 — Initial release

- Spring Boot + Jersey (JAX-RS) backend with Spring Data JPA, PostgreSQL, Flyway-managed schema.
- JWT auth (Spring Security) over a multi-user-capable schema, seeded with one user (`pkasula`).
- Fully dynamic Tracks/Topics/References, seeded with 3 starter tracks (Java, System Design, AI)
  and ~12 topics each with real reference links.
- Configurable daily hour budgets per day type (weekday/weekend/holiday) and per-track allocation,
  with non-fatal mismatch warnings.
- Daily per-track goal logging with immutable planned-hours snapshots, plus per-topic
  study/practice logging and aggregate progress tracking.
- Dashboard ("today" view), adherence/hours/coverage reports, and streak tracking.
- Deterministic pace/ETA/coverage predictions, with an optional Spring AI (local Ollama by
  default, provider-swappable) narrative layered on top and graceful stats-only fallback.
- React + TypeScript frontend: dashboard, track/topic management, hour/allocation config,
  holidays, reports (charts), and predictions pages.
- Theming: light/dark mode plus a swappable color-palette theme ("Default", "Ocean"), both
  persisted per browser.
