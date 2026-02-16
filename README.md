# PPM Backend

Enterprise Project & Performance Management backend using Java 21, Spring Boot, PostgreSQL, stateless JWT auth, and RBAC.

## Architecture

Hexagonal/Clean layering:

- `domain`: business models, use-case ports, outbound ports, domain exceptions
- `application`: use-case implementations and API DTO contracts
- `adapters.in.web`: REST controllers
- `adapters.out.persistence`: JPA entities, repositories, persistence adapters
- `adapters.out.security`: password hashing and JWT issuance adapters
- `infrastructure`: security config, exception handling, and runtime configuration

## Key Features

- Projects, tasks, milestones, KPI/report endpoints
- Stateless authentication (`/api/v1/auth/login`, `/api/v1/auth/refresh`, `/api/v1/auth/me`)
- Server-backed task list endpoint (`GET /api/v1/tasks`) with pagination + filters/search
- Project list filters (`GET /api/v1/projects?status=...&q=...`)
- Permission-based RBAC + object-level project membership checks (IDOR protection)
- Flyway schema + RBAC seed migrations
- RFC7807-style error responses via centralized `@RestControllerAdvice`
- BCrypt password hashing, refresh token hashing + rotation
- Audit trail persistence for key actions

## Environment Variables

- `PPM_DB_URL` (default `jdbc:postgresql://localhost:5432/ppm`)
- `PPM_DB_USERNAME` (default `postgres`)
- `PPM_DB_PASSWORD` (default `michael`)
- `PPM_JWT_SECRET` (must be at least 32 chars)
- `PPM_ACCESS_TOKEN_TTL` (default `PT15M`)
- `PPM_REFRESH_TOKEN_TTL` (default `P30D`)
- `PPM_ALLOWED_ORIGINS` (comma-separated; default local React dev origins)
- `PPM_BOOTSTRAP_ADMIN_ENABLED` (`true|false`)
- `PPM_BOOTSTRAP_ADMIN_EMAIL`
- `PPM_BOOTSTRAP_ADMIN_PASSWORD`
- `SPRING_PROFILES_ACTIVE` (`prod` in cloud deploys to enforce production fail-fast checks)
- `PORT` (default `8080`; Render sets this automatically)

## Run

```bash
./mvnw spring-boot:run
```

## Docker

```bash
docker build -t ppm-backend .
docker run --rm -p 8080:8080 \
  -e PPM_DB_URL=jdbc:postgresql://host.docker.internal:5432/ppm \
  -e PPM_DB_USERNAME=postgres \
  -e PPM_DB_PASSWORD=postgres \
  -e PPM_JWT_SECRET=replace-with-32-char-secret \
  ppm-backend
```

## Deploy To Render

This repository now includes:

- `Dockerfile` for containerized builds
- `.dockerignore` to keep Docker context small
- `render.yaml` Blueprint with:
  - one `web` service using `runtime: docker`
  - one managed PostgreSQL database (`ppm-postgres`)
  - environment variables wired from the database and secrets

Steps:

1. Push this repo to GitHub/GitLab/Bitbucket.
2. In Render, create from Blueprint (`render.yaml`) or create a Web Service from this repo.
3. Set secret values for `PPM_JWT_SECRET`, `PPM_ALLOWED_ORIGINS`, `PPM_BOOTSTRAP_ADMIN_EMAIL`, and `PPM_BOOTSTRAP_ADMIN_PASSWORD`.
4. Deploy and verify `GET /actuator/health` returns `200`.
