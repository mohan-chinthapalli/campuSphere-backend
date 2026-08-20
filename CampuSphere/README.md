# CampuSphere — Backend

**Smart Campus Management System** — Spring Boot REST API backend.

This backend serves the existing [CampuSphere frontend](https://github.com/mohan-chinthapalli/campusphereio/tree/frontend).

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.5 |
| Database | MySQL 8.0 |
| Auth | JWT (jjwt 0.12.6) |
| Migrations | Flyway |
| Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven |
| Tests | JUnit 5, Mockito |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0 (or Docker)

---

## Quick Start (Local)

### 1. Clone

```bash
git clone https://github.com/mohan-chinthapalli/campuSphere-backend.git
cd campuSphere-backend/CampuSphere
```

### 2. Create MySQL database

```sql
CREATE DATABASE campusphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure environment

```bash
cp .env.example .env
# Edit .env — set DB_PASSWORD and JWT_SECRET at minimum
```

Key variables:

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/campusphere?...` | MySQL JDBC URL |
| `DB_USERNAME` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `JWT_SECRET` | *(change this!)* | JWT signing secret (32+ chars) |
| `FRONTEND_URL` | `http://localhost:3000` | Frontend origin for CORS |

### 4. Run

```bash
./mvnw spring-boot:run
```

Or with explicit environment variables:

```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DDB_PASSWORD=yourpass -DJWT_SECRET=yoursecret"
```

The server starts on **http://localhost:8080**

---

## Quick Start (Docker)

```bash
cp .env.example .env
# Edit .env

docker-compose up -d
```

Services:
- Backend: http://localhost:8080
- MySQL: localhost:3306

---

## Swagger / API Docs

Available at: **http://localhost:8080/swagger-ui.html**

OpenAPI JSON: http://localhost:8080/api-docs

---

## Database Migrations

Flyway runs automatically on startup. Migration files are in:

```
src/main/resources/db/migration/
├── V1__initial_schema.sql      # Users, profiles, campus places
├── V2__events_clubs.sql        # Events, clubs, memberships
├── V3__learning_hub.sql        # Subjects, materials, sessions
├── V4__announcements_notifications.sql
├── V5__mentorship_feedback.sql # Mentorship, feedback, timetable, deadlines
├── V6__ai_conversations.sql    # AI conversations and messages
└── V7__seed_data.sql           # Demo seed data
```

---

## Demo Accounts

> ⚠️ For development only. Never use these in production.

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

Tests use H2 in-memory database — no MySQL required.

---

## Project Structure

```
src/main/java/com/major/CampuSphere/
├── config/          # Security, JPA auditing, OpenAPI
├── controller/      # REST controllers (HTTP layer only)
├── dto/
│   ├── request/     # Incoming request DTOs
│   └── response/    # Outgoing response DTOs
├── entity/          # JPA entities
├── enums/           # Role, MaterialType, Priority, etc.
├── exception/       # GlobalExceptionHandler + custom exceptions
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JwtUtil, principal
└── service/
    └── impl/        # Business logic implementations
```

---

## API Overview

| Module | Base Path |
|---|---|
| Auth | `POST /api/auth/login`, `POST /api/auth/register` |
| Events | `GET/POST /api/events`, `POST /api/events/{slug}/register` |
| Clubs | `GET/POST /api/clubs`, `POST /api/clubs/{slug}/join` |
| Announcements | `GET /api/announcements` |
| Learning Hub | `GET /api/materials`, `GET /api/subjects` |
| Skill Sessions | `GET /api/sessions`, `POST /api/sessions/{slug}/enroll` |
| Mentorship | `GET /api/mentors`, `POST /api/mentors/request` |
| Academics | `GET /api/academics/dashboard` |
| Profile | `GET/PUT /api/profile` |
| Notifications | `GET /api/notifications` |
| Feedback | `POST /api/feedback/platform`, `POST /api/feedback/faculty` |
| Campus Navigation | `GET /api/places` |
| AI | `POST /api/ai/ask-doubt`, `POST /api/ai/chat` |

Full documentation: **Swagger UI** at `/swagger-ui.html`

---

## AI Integration

The AI module currently runs in **demo mode** — no external LLM is called.

To connect a real LLM later:
1. Create `RealAiServiceImpl implements AiService`
2. Annotate it `@Primary`
3. Remove `@Primary` from `DemoAiServiceImpl`
4. The controller and API contract remain unchanged

---

## Health Check

```
GET /actuator/health
```
