# CampuSphere — Backend

**Smart Campus Management System** — Spring Boot REST API backend.

This backend serves the existing [CampuSphere frontend](https://github.com/mohan-chinthapalli/campusphereio).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Database | **Supabase PostgreSQL** |
| ORM | Spring Data JPA / Hibernate 6 |
| Auth | JWT (jjwt 0.12.6) |
| Migrations | Flyway |
| Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven |
| Tests | JUnit 5, Mockito, H2 (PostgreSQL mode) |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- A [Supabase](https://supabase.com) project (free tier works)

No local database installation required — the database is Supabase PostgreSQL (hosted).

---

## Quick Start

### 1. Clone

```bash
git clone https://github.com/mohan-chinthapalli/campuSphere-backend.git
cd campuSphere-backend/CampuSphere
```

### 2. Create a Supabase project

1. Go to [supabase.com](https://supabase.com) and create a new project
2. Wait for provisioning to complete
3. Go to **Settings → Database** to find your connection details

### 3. Configure environment variables

```bash
cp .env.example .env
# Edit .env with your Supabase credentials
```

Required variables:

| Variable | Where to find it | Example |
|---|---|---|
| `SUPABASE_DB_URL` | Settings → Database → Connection string → JDBC | `jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require` |
| `SUPABASE_DB_USERNAME` | Settings → Database → User | `postgres` |
| `SUPABASE_DB_PASSWORD` | Settings → Database → Password | your-db-password |
| `JWT_SECRET` | Generate a random string (32+ chars) | — |
| `FRONTEND_URL` | Your frontend origin | `http://localhost:3000` |

> ⚠️ Use the **Direct connection** (port 5432) for this Spring Boot backend. Always include `?sslmode=require` in the URL.

### 4. Run

**PowerShell (Windows):**
```powershell
$env:SUPABASE_DB_URL="jdbc:postgresql://db.xxxx.supabase.co:5432/postgres?sslmode=require"
$env:SUPABASE_DB_USERNAME="postgres"
$env:SUPABASE_DB_PASSWORD="your-password"
$env:JWT_SECRET="your-32-char-secret"
.\mvnw.cmd spring-boot:run
```

**Or load from `.env` file then run:**
```powershell
Get-Content .env | ForEach-Object {
  if ($_ -match '^([^#][^=]+)=(.+)$') {
    [System.Environment]::SetEnvironmentVariable($Matches[1].Trim(), $Matches[2].Trim())
  }
}
.\mvnw.cmd spring-boot:run
```

The server starts on **http://localhost:8080**.

Flyway runs automatically on startup and creates all tables + seed data in Supabase.

---

## Quick Start (Docker)

```bash
cp .env.example .env
# Edit .env with Supabase credentials

docker-compose up -d
```

No local database container — Docker Compose runs only the Spring Boot backend. Database is Supabase (remote).

---

## Swagger / API Docs

Available at: **http://localhost:8080/swagger-ui.html**

OpenAPI JSON: **http://localhost:8080/api-docs**

---

## Database Migrations

Flyway runs automatically on startup and applies all migrations in order.

```
src/main/resources/db/migration/
├── V1__initial_schema.sql           # users, student_profiles, faculty_profiles, campus_places
├── V2__events_clubs.sql             # events, clubs, memberships
├── V3__learning_hub.sql             # subjects, learning_materials, skill_sessions
├── V4__announcements_notifications.sql
├── V5__mentorship_feedback.sql      # mentorship, feedback, timetable, deadlines
├── V6__ai_conversations.sql         # AI conversation history
├── V7__seed_data.sql                # Demo seed data
└── V8__rename_year_column.sql       # No-op on PostgreSQL (historical parity)
```

All migrations are **PostgreSQL-native** (no MySQL syntax).

---

## Demo Accounts

> ⚠️ Development only. These are seeded by V7 — never use in production.

| Role | Email | Password |
|---|---|---|
| Admin | `admin@campusphere.edu` | `Demo@1234` |
| Student | `student@campusphere.edu` | `Demo@1234` |
| Faculty | `faculty@campusphere.edu` | `Demo@1234` |

---

## Running Tests

```bash
mvn test
```

Tests use **H2 in-memory database in PostgreSQL-compatibility mode**. No Supabase connection required for tests.

```
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0  ✓
```

---

## Project Structure

```
src/main/java/com/major/CampuSphere/
├── config/          # SecurityConfig, OpenApiConfig, JpaAuditingConfig
├── controller/      # REST controllers (HTTP layer only)
├── dto/
│   ├── request/     # Validated incoming request DTOs
│   └── response/    # Outgoing response DTOs (ApiResponse envelope)
├── entity/          # JPA entities (GenerationType.IDENTITY, EnumType.STRING)
├── enums/           # Role, MaterialType, Priority, NotificationType, etc.
├── exception/       # GlobalExceptionHandler + typed exception classes
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JwtUtil, CampuSpherePrincipal
└── service/
    └── impl/        # Business logic (AuthService, EventService, AiService, etc.)
```

---

## API Overview

| Module | Endpoints |
|---|---|
| Auth | `POST /api/auth/login`, `/register`, `/logout`, `GET /api/auth/me` |
| Events | `GET/POST /api/events`, `POST /api/events/{slug}/register` |
| Clubs | `GET/POST /api/clubs`, `POST /api/clubs/{slug}/join` |
| Announcements | `GET /api/announcements`, `POST /api/announcements` |
| Learning Hub | `GET /api/materials`, `GET /api/subjects`, `PATCH /api/materials/{id}/progress` |
| Skill Sessions | `GET /api/sessions`, `POST /api/sessions/{slug}/enroll` |
| Mentorship | `GET /api/mentors`, `POST /api/mentors/request` |
| Academics | `GET /api/academics/dashboard`, `/timetable`, `/deadlines` |
| Profile | `GET/PUT /api/profile`, `/profile/student`, `/profile/faculty` |
| Notifications | `GET /api/notifications`, `PATCH /api/notifications/read-all` |
| Feedback | `POST /api/feedback/platform`, `/feedback/faculty` |
| Campus Navigation | `GET /api/places` |
| AI | `POST /api/ai/ask-doubt`, `/ai/chat`, `GET /api/ai/conversations` |

Full interactive documentation: **http://localhost:8080/swagger-ui.html**

---

## AI Integration

The AI module runs in **demo mode** — no external LLM API is called.

To connect a real LLM:
1. Create `RealAiServiceImpl implements AiService`
2. Annotate it `@Primary`
3. Remove `@Primary` from `DemoAiServiceImpl`
4. The controller and API contract remain **unchanged**

---

## Health Check

```
GET /actuator/health
```

---

## Security Notes

- Passwords hashed with BCrypt (strength 12)
- JWT tokens expire in 24h by default
- All secrets via environment variables — never hardcoded
- CORS restricted to `FRONTEND_URL`
- `spring.jpa.hibernate.ddl-auto=validate` — Flyway owns schema
