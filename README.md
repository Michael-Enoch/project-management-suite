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
- Stateless authentication (`/api/v1/auth/login`, `/api/v1/auth/refresh`)
- Permission-based RBAC + object-level project membership checks (IDOR protection)
- Flyway schema + RBAC seed migrations
- RFC7807-style error responses via centralized `@RestControllerAdvice`
- BCrypt password hashing, refresh token hashing + rotation
- Audit trail persistence for key actions

## Environment Variables

- `PPM_DB_URL` (default `jdbc:postgresql://localhost:5432/ppm`)
- `PPM_DB_USERNAME` (default `postgres`)
- `PPM_DB_PASSWORD` (default `postgres`)
- `PPM_JWT_SECRET` (must be at least 32 chars)
- `PPM_ACCESS_TOKEN_TTL` (default `PT15M`)
- `PPM_REFRESH_TOKEN_TTL` (default `P30D`)
- `PPM_ALLOWED_ORIGINS` (comma-separated; default local React dev origins)
- `PPM_BOOTSTRAP_ADMIN_ENABLED` (`true|false`)
- `PPM_BOOTSTRAP_ADMIN_EMAIL`
- `PPM_BOOTSTRAP_ADMIN_PASSWORD`

## Run

```bash
./mvnw spring-boot:run
```
