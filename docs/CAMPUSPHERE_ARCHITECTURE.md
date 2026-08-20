# CampuSphere — Backend Architecture

## 1. Overview

CampuSphere is a **modular monolithic Spring Boot REST API** that serves the CampuSphere React/TanStack frontend. It follows a strict layered architecture with no business logic in controllers and no entity exposure in API responses.

---

## 2. Technology Stack

| Component | Technology | Version |
|---|---|---|
| Language | Java | 17 |
| Framework | Spring Boot | 3.3.5 |
| Build Tool | Maven | 3.8+ |
| Database | MySQL | 8.0 |
| ORM | Spring Data JPA / Hibernate | Managed by Boot |
| Migrations | Flyway | 10.15.0 |
| Security | Spring Security + JWT (jjwt) | 0.12.6 |
| API Docs | SpringDoc OpenAPI / Swagger UI | 2.6.0 |
| Containerization | Docker + Docker Compose | — |
| Testing | JUnit 5 + Mockito + H2 | Managed by Boot |

---

## 3. Package Structure

```
com.major.CampuSphere/
├── CampuSphereApplication.java       ← Main entry point
│
├── config/
│   ├── JpaAuditingConfig.java        ← Enables @CreatedDate / @LastModifiedDate
│   ├── OpenApiConfig.java            ← Swagger/OpenAPI configuration
│   └── SecurityConfig.java           ← Spring Security, CORS, JWT filter chain
│
├── controller/                       ← HTTP layer only — no business logic
│   ├── AuthController.java
│   ├── AcademicsController.java
│   ├── AiController.java
│   ├── AnnouncementController.java
│   ├── CampusNavigationController.java
│   ├── ClubController.java
│   ├── EventController.java
│   ├── FeedbackController.java
│   ├── LearningHubController.java
│   ├── MentorshipController.java
│   ├── NotificationController.java
│   ├── ProfileController.java
│   └── SkillSessionController.java
│
├── service/
│   ├── AiService.java                ← Interface (swap DemoAI → RealAI here)
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── AcademicsServiceImpl.java
│       ├── AnnouncementServiceImpl.java
│       ├── CampusNavigationServiceImpl.java
│       ├── ClubServiceImpl.java
│       ├── DemoAiServiceImpl.java    ← @Primary — no real LLM connected
│       ├── EventServiceImpl.java
│       ├── FeedbackServiceImpl.java
│       ├── LearningHubServiceImpl.java
│       ├── MentorshipServiceImpl.java
│       ├── NotificationServiceImpl.java
│       ├── ProfileServiceImpl.java
│       └── SkillSessionServiceImpl.java
│
├── repository/                       ← Spring Data JPA interfaces
│   ├── AiConversationRepository.java
│   ├── AnnouncementRepository.java
│   ├── CampusPlaceRepository.java
│   ├── ClubMembershipRepository.java
│   ├── ClubRepository.java
│   ├── DeadlineRepository.java
│   ├── EventDiscussionRepository.java
│   ├── EventRegistrationRepository.java
│   ├── EventRepository.java
│   ├── FacultyProfileRepository.java
│   ├── LearningMaterialRepository.java
│   ├── MaterialProgressRepository.java
│   ├── MentorProfileRepository.java
│   ├── MentorshipRequestRepository.java
│   ├── NotificationRepository.java
│   ├── SessionEnrollmentRepository.java
│   ├── SkillSessionRepository.java
│   ├── StudentProfileRepository.java
│   ├── StudentSkillRepository.java
│   ├── SubjectRepository.java
│   ├── TimetableEntryRepository.java
│   └── UserRepository.java
│
├── entity/                           ← JPA entities — never exposed in API directly
│   ├── AiConversation.java
│   ├── AiMessage.java
│   ├── Announcement.java
│   ├── CampusPlace.java
│   ├── Club.java
│   ├── ClubAchievement.java
│   ├── ClubLead.java
│   ├── ClubMembership.java
│   ├── Deadline.java
│   ├── Event.java
│   ├── EventAgenda.java
│   ├── EventDiscussion.java
│   ├── EventRegistration.java
│   ├── FacultyProfile.java
│   ├── LearningMaterial.java
│   ├── MaterialProgress.java
│   ├── MentorProfile.java
│   ├── MentorshipRequest.java
│   ├── Notification.java
│   ├── SessionEnrollment.java
│   ├── SkillSession.java
│   ├── SkillSessionOutcome.java
│   ├── StudentProfile.java
│   ├── StudentSkill.java
│   ├── Subject.java
│   ├── TimetableEntry.java
│   └── User.java
│
├── dto/
│   ├── request/                      ← Validated incoming request bodies
│   │   ├── AiChatRequest.java
│   │   ├── AskDoubtRequest.java
│   │   ├── CreateAnnouncementRequest.java
│   │   ├── CreateEventRequest.java
│   │   ├── FeedbackFacultyRequest.java
│   │   ├── FeedbackPlatformRequest.java
│   │   ├── LoginRequest.java
│   │   ├── MentorshipRequestDto.java
│   │   ├── RegisterRequest.java
│   │   ├── UpdateFacultyProfileRequest.java
│   │   ├── UpdateProgressRequest.java
│   │   └── UpdateStudentProfileRequest.java
│   └── response/                     ← Outgoing response shapes
│       ├── ApiResponse.java          ← Universal envelope
│       ├── PageResponse.java         ← Pagination wrapper
│       ├── AiResponse.java
│       ├── AnnouncementResponse.java
│       ├── AuthResponse.java
│       ├── CampusPlaceResponse.java
│       ├── ClubResponse.java
│       ├── ConversationResponse.java
│       ├── DashboardResponse.java
│       ├── DeadlineResponse.java
│       ├── EventResponse.java
│       ├── FacultyProfileResponse.java
│       ├── MaterialResponse.java
│       ├── MentorResponse.java
│       ├── NotificationResponse.java
│       ├── SkillSessionResponse.java
│       ├── StudentProfileResponse.java
│       ├── SubjectResponse.java
│       ├── TimetableResponse.java
│       └── UserResponse.java
│
├── enums/
│   ├── AnnouncementTag.java
│   ├── ClubRole.java
│   ├── MaterialType.java
│   ├── MentorshipStatus.java
│   ├── NotificationType.java
│   ├── Priority.java
│   ├── Role.java
│   ├── SessionLevel.java
│   └── Urgency.java
│
├── exception/
│   ├── BadRequestException.java
│   ├── CampuSphereException.java     ← Base exception
│   ├── DuplicateResourceException.java
│   ├── ForbiddenException.java
│   ├── GlobalExceptionHandler.java   ← @RestControllerAdvice
│   └── ResourceNotFoundException.java
│
└── security/
    ├── CampuSpherePrincipal.java     ← Authenticated principal (userId, email, role)
    ├── JwtAuthFilter.java            ← OncePerRequestFilter
    ├── JwtProperties.java            ← @ConfigurationProperties(app.jwt.*)
    ├── JwtUtil.java                  ← Token generation and validation
    └── UserDetailsServiceImpl.java   ← Loads user from DB for auth manager
```

---

## 4. Request Flow

```
React Frontend (TanStack Router + TanStack Query)
        │
        │  HTTP/JSON  Authorization: Bearer <JWT>
        ▼
Spring Boot Backend :8080
        │
        ├─► JwtAuthFilter (OncePerRequestFilter)
        │       ├─ Extract JWT from Authorization header
        │       ├─ Validate signature + expiry (JwtUtil)
        │       ├─ Create CampuSpherePrincipal { userId, email, role }
        │       └─ Store in SecurityContextHolder
        │
        ├─► SecurityFilterChain
        │       ├─ Permit public endpoints (GET /api/events, etc.)
        │       └─ Require auth for everything else
        │
        ▼
    Controller  (HTTP layer: request mapping, validation, response wrapping)
        │
        ▼
    Service     (Business logic, transaction boundaries)
        │
        ├─► Repository  (Spring Data JPA queries)
        │       │
        │       ▼
        │   MySQL 8.0  (Flyway-managed schema)
        │
        └─► Other Services (e.g. NotificationService called from EventService)
        │
        ▼
    DTO         (Response mapped from Entity, never expose Entity directly)
        │
        ▼
    ApiResponse<T>  { success, message, data, timestamp }
        │
        ▼
React Frontend
```

---

## 5. Database Architecture

### Migration Files
| File | Contents |
|---|---|
| `V1__initial_schema.sql` | users, student_profiles, faculty_profiles, student_skills, campus_places |
| `V2__events_clubs.sql` | events, event_agenda, event_registrations, event_discussions, clubs, club_achievements, club_leads, club_memberships |
| `V3__learning_hub.sql` | subjects, learning_materials, material_progress, skill_sessions, skill_session_outcomes, session_enrollments |
| `V4__announcements_notifications.sql` | announcements, notifications |
| `V5__mentorship_feedback.sql` | mentor_profiles, mentorship_requests, feedback_platform, feedback_faculty, timetable_entries, deadlines |
| `V6__ai_conversations.sql` | ai_conversations, ai_messages |
| `V7__seed_data.sql` | Demo data — 10 users, events, clubs, sessions, materials, notifications |

### Entity Relationship Summary
```
users ─────────────────────────────────────────────────────────────────
  │── (1:1) student_profiles
  │       └── student_skills (1:N)
  │── (1:1) faculty_profiles
  │── (1:1) mentor_profiles
  │── (1:N) timetable_entries
  │── (1:N) deadlines
  │── (1:N) notifications
  │── (1:N) event_registrations  ──► events
  │── (1:N) club_memberships     ──► clubs
  │── (1:N) session_enrollments  ──► skill_sessions
  │── (1:N) material_progress    ──► learning_materials
  │── (1:N) mentorship_requests (as mentor or mentee)
  │── (1:N) ai_conversations
  │── (1:N) feedback_platform
  └── (1:N) event_discussions    ──► events

events
  └── (1:N) event_agenda
  └── (1:N) event_registrations
  └── (1:N) event_discussions

clubs
  └── (1:N) club_memberships
  └── (1:N) club_achievements
  └── (1:N) club_leads

subjects ──► (FK) users (faculty)
learning_materials ──► (FK) subjects.code
  └── (1:N) material_progress

skill_sessions ──► (FK) users (faculty)
  └── (1:N) skill_session_outcomes
  └── (1:N) session_enrollments

ai_conversations ──► users, learning_materials
  └── (1:N) ai_messages
```

---

## 6. Authentication Flow

```
1. POST /api/auth/login  { email, password }
        │
        ▼
2. AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)
        │
        ▼
3. UserDetailsServiceImpl.loadUserByUsername(email)
        │   → SELECT FROM users WHERE email = ?
        │   → Returns UserDetails with ROLE_{role} authority
        │
        ▼
4. BCrypt password verification (BCryptPasswordEncoder strength=12)
        │
        ▼
5. JwtUtil.generateToken(userId, email, role, name)
        │   → JWT Claims: { sub: userId, email, role, name, iat, exp }
        │   → Signed with HMAC-SHA256 (key from app.jwt.secret)
        │   → Expires in 86400000ms (24h default)
        │
        ▼
6. Return AuthResponse { token, role, name, email, userId }
        │
        ▼
7. Frontend stores token (localStorage / memory)
        │
        ▼
8. Subsequent requests: Authorization: Bearer <token>
        │
        ▼
9. JwtAuthFilter intercepts every request
        │   → Extracts Bearer token
        │   → JwtUtil.isTokenValid() → verifies signature + expiry
        │   → Extracts userId, email, role from claims
        │   → Creates CampuSpherePrincipal
        │   → Sets SecurityContextHolder
        │
        ▼
10. Controller receives @AuthenticationPrincipal CampuSpherePrincipal
```

---

## 7. Authorization (RBAC)

| Role | Prefix | Capabilities |
|---|---|---|
| `STUDENT` | `ROLE_STUDENT` | Browse all public resources; register for events; join clubs; enroll in sessions; use AI; manage own profile; view own academics |
| `FACULTY` | `ROLE_FACULTY` | All student capabilities + create/delete events; create announcements; manage own faculty profile |
| `ADMIN` | `ROLE_ADMIN` | All faculty capabilities + delete any announcement |

### Endpoint Authorization Matrix

| Endpoint | Method | Auth | STUDENT | FACULTY | ADMIN |
|---|---|---|---|---|---|
| `/api/auth/**` | ANY | None | ✅ | ✅ | ✅ |
| `/api/events` | GET | None | ✅ | ✅ | ✅ |
| `/api/events` | POST | Required | ❌ | ✅ | ✅ |
| `/api/events/{slug}` | DELETE | Required | ❌ | ✅ (own) | ✅ |
| `/api/events/{slug}/register` | POST/DELETE | Required | ✅ | ✅ | ✅ |
| `/api/clubs` | GET | None | ✅ | ✅ | ✅ |
| `/api/clubs/{slug}/join` | POST/DELETE | Required | ✅ | ✅ | ✅ |
| `/api/announcements` | GET | None | ✅ | ✅ | ✅ |
| `/api/announcements` | POST | Required | ❌ | ✅ | ✅ |
| `/api/announcements/{id}` | DELETE | Required | ❌ | ❌ | ✅ |
| `/api/materials/**` | GET | None | ✅ | ✅ | ✅ |
| `/api/materials/{id}/progress` | PATCH | Required | ✅ | ✅ | ✅ |
| `/api/sessions` | GET | None | ✅ | ✅ | ✅ |
| `/api/sessions/{slug}/enroll` | POST/DELETE | Required | ✅ | ✅ | ✅ |
| `/api/mentors` | GET | Required | ✅ | ✅ | ✅ |
| `/api/mentors/request` | POST | Required | ✅ | ✅ | ✅ |
| `/api/academics/**` | GET | Required (STUDENT) | ✅ | ❌ | ❌ |
| `/api/profile` | GET/PUT | Required | ✅ | ✅ | ✅ |
| `/api/profile/student` | GET/PUT | Required (STUDENT) | ✅ | ❌ | ❌ |
| `/api/profile/faculty` | GET/PUT | Required (FACULTY) | ❌ | ✅ | ❌ |
| `/api/profile/faculty/{id}` | GET | Required | ✅ | ✅ | ✅ |
| `/api/notifications/**` | ANY | Required | ✅ | ✅ | ✅ |
| `/api/feedback/**` | POST | Required | ✅ | ✅ | ✅ |
| `/api/places/**` | GET | None | ✅ | ✅ | ✅ |
| `/api/ai/**` | ANY | Required | ✅ | ✅ | ✅ |

---

## 8. Security Configuration

- **CSRF**: Disabled (stateless JWT API)
- **Sessions**: `STATELESS`
- **CORS**: Configurable via `FRONTEND_URL` env var (default: `localhost:3000,localhost:5173`)
- **Password hashing**: BCrypt strength 12
- **JWT algorithm**: HMAC-SHA256
- **JWT expiry**: 24h (configurable via `JWT_EXPIRATION_MS`)
- **No secrets in source code**: All secrets via environment variables

---

## 9. AI Module Architecture

```
Frontend (AI Doubt Panel or Chat)
        │
        │  POST /api/ai/ask-doubt  or  POST /api/ai/chat
        ▼
AiController
        │
        ▼
AiService (interface)           ← Swap point for real LLM
        │
        ▼
DemoAiServiceImpl (@Primary)    ← Current: keyword-matched canned responses
        │
        ├─ Creates/reuses AiConversation (UUID key)
        ├─ Persists user message as AiMessage
        ├─ Generates contextual demo response
        └─ Persists assistant message as AiMessage
        │
        ▼
Return AiResponse { conversationId, answer, reply, sources, demo: true }

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Future RAG architecture (not yet implemented):

AiController
        │
        ▼
AiService
        │
        ▼
RealAiServiceImpl (@Primary — replaces Demo)
        │
        ├─► DocumentProcessor → Text extraction from PDFs
        ├─► EmbeddingService  → Vector embeddings
        ├─► VectorStore       → Similarity search
        ├─► Retriever         → Relevant document chunks
        └─► LLMService        → OpenAI / Anthropic / local model
                │
                ▼
        Answer + sources (document references)
```

To enable real LLM:
1. Create `RealAiServiceImpl implements AiService`
2. Annotate with `@Primary`
3. Remove `@Primary` from `DemoAiServiceImpl`
4. Controller and API contract are **unchanged**

---

## 10. Configuration & Environment Variables

| Variable | Default | Required | Description |
|---|---|---|---|
| `PORT` | `8080` | No | Server port |
| `DB_URL` | `jdbc:mysql://localhost:3306/campusphere?...` | **YES** | Full JDBC URL |
| `DB_USERNAME` | `root` | **YES** | MySQL username |
| `DB_PASSWORD` | `root` | **YES** | MySQL password |
| `JWT_SECRET` | `campusphere-super-secret...` | **YES** | JWT signing secret (32+ chars) |
| `JWT_EXPIRATION_MS` | `86400000` | No | Token expiry in ms (24h) |
| `JWT_REFRESH_EXPIRATION_MS` | `604800000` | No | Refresh token expiry (7d) |
| `FRONTEND_URL` | `http://localhost:3000,http://localhost:5173` | No | CORS allowed origins |

> ⚠️ **Never commit real values.** Use `.env` locally. Use secrets manager in production.

---

## 11. Database Performance Notes

- All list endpoints use `Page<T>` with configurable `page`/`size`
- `N+1` risk mitigated in `EventServiceImpl` — single `countByEventId` per event
- `LearningMaterial` search uses indexed columns (`semester`, `material_type`, `subject_code`)
- `event_registrations` has composite unique index `(event_id, user_id)` — prevents duplicate registration at DB level
- `Club.memberCount` is a denormalized counter (updated on join/leave) — avoids COUNT query on every list
- All entities use `BIGINT UNSIGNED AUTO_INCREMENT` primary keys
- `FetchType.LAZY` used throughout — no accidental eager loading

---

## 12. Demo Accounts

> ⚠️ Development only. Password: `Demo@1234` for all accounts.

| Role | Email |
|---|---|
| ADMIN | `admin@campusphere.edu` |
| STUDENT | `student@campusphere.edu` |
| FACULTY | `faculty@campusphere.edu` |
| STUDENT | `s2@campusphere.edu` (Riya Patel) |
| FACULTY | `f2@campusphere.edu` (Prof. Suresh Kumar) |
