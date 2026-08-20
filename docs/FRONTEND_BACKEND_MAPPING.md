# CampuSphere — Frontend → Backend Mapping

Generated from frontend audit of: https://github.com/mohan-chinthapalli/campusphereio/tree/frontend

---

## A. All Frontend Routes

| Route | Page | Role | Purpose | Backend APIs |
|---|---|---|---|---|
| `/` | Landing | Public | Marketing page | None |
| `/login` | Login | Public | Email+password / Google auth | `POST /api/auth/login` |
| `/forgot-password` | Forgot Password | Public | Password reset | `POST /api/auth/forgot-password` *(future)* |
| `/app` | Dashboard | STUDENT | Academic overview, deadlines, events | `GET /api/academics/dashboard` |
| `/app/events` | Events Explorer | Any | Browse, filter, search events | `GET /api/events` |
| `/app/events/$eventId` | Event Detail | Any | Event info, register, discuss | `GET /api/events/{slug}`, `POST /api/events/{slug}/register` |
| `/app/clubs` | Clubs Explorer | Any | Browse, filter clubs | `GET /api/clubs` |
| `/app/clubs/$clubId` | Club Detail | Any | Club info, join/leave | `GET /api/clubs/{slug}`, `POST /api/clubs/{slug}/join` |
| `/app/navigate` | Campus Navigation | Any | Interactive campus map | `GET /api/places` |
| `/app/announcements` | Announcements | Any | Official campus news | `GET /api/announcements` |
| `/app/learn` | Learning Hub | Any | Material browser by semester/subject | `GET /api/materials`, `GET /api/subjects` |
| `/app/learn/$materialId` | Material Reader | Any | Read material + AI Doubt Panel | `GET /api/materials/key/{key}`, `POST /api/ai/ask-doubt` |
| `/app/skills` | Faculty Skill Hub | Any | Browse skill sessions | `GET /api/sessions` |
| `/app/mentorship` | Mentorship | Any | Find and request mentors | `GET /api/mentors`, `POST /api/mentors/request` |
| `/app/ai` | Campus AI Chat | Auth | General campus AI chatbot | `POST /api/ai/chat` |
| `/app/academics` | My Academics | STUDENT | CGPA, attendance, timetable, deadlines | `GET /api/academics/dashboard`, `GET /api/academics/timetable` |
| `/app/profile` | Profile | Auth | View/edit profile and skills | `GET /api/profile`, `PUT /api/profile/student` |
| `/app/feedback` | Feedback | Auth | Platform + faculty feedback | `POST /api/feedback/platform`, `POST /api/feedback/faculty` |
| `/app/settings` | Settings | Auth | Profile edit, notification preferences | `GET /api/profile`, `PUT /api/profile` |

---

## B. Mock Data → Backend Entities

| Mock Data Source | Represents | MySQL Table | REST API |
|---|---|---|---|
| `events[]` in `data.ts` | Campus events | `events`, `event_registrations`, `event_agenda` | `GET /api/events` |
| `clubs[]` in `data.ts` | Campus clubs | `clubs`, `club_memberships`, `club_leads`, `club_achievements` | `GET /api/clubs` |
| `sessions[]` in `data.ts` | Faculty skill sessions | `skill_sessions`, `session_enrollments` | `GET /api/sessions` |
| `mentors[]` in `data.ts` | Senior student mentors | `mentor_profiles`, `mentorship_requests` | `GET /api/mentors` |
| `materials[]` generated | Learning materials | `learning_materials`, `material_progress` | `GET /api/materials` |
| `semesterSubjects[]` | Academic subjects | `subjects` | `GET /api/subjects` |
| `announcements[]` | Campus announcements | `announcements` | `GET /api/announcements` |
| `student` mock | Logged-in student | `users`, `student_profiles` | `GET /api/academics/dashboard` |
| `todayClasses[]` | Timetable | `timetable_entries` | `GET /api/academics/timetable` |
| `deadlines[]` | Student deadlines | `deadlines` | `GET /api/academics/deadlines` |
| `campusPlaces[]` | Navigation map | `campus_places` | `GET /api/places` |
| `auth-mock.ts` functions | Authentication | `users` + JWT | `POST /api/auth/login` |

---

## C. Frontend Feature → Backend Module Table

| Frontend Feature | Backend Module | Entity | API | Role |
|---|---|---|---|---|
| Login with email/password | Auth | User | `POST /api/auth/login` | Public |
| Register new account | Auth | User, StudentProfile / FacultyProfile | `POST /api/auth/register` | Public |
| Get current user | Auth | User | `GET /api/auth/me` | Any auth |
| Student dashboard | Academics | StudentProfile, TimetableEntry, Deadline | `GET /api/academics/dashboard` | STUDENT |
| Timetable | Academics | TimetableEntry | `GET /api/academics/timetable` | STUDENT |
| Deadlines | Academics | Deadline | `GET /api/academics/deadlines` | STUDENT |
| Browse events | Events | Event | `GET /api/events` | Public |
| Event detail | Events | Event, EventAgenda | `GET /api/events/{slug}` | Public |
| Register for event | Events | EventRegistration | `POST /api/events/{slug}/register` | Auth |
| Create event | Events | Event | `POST /api/events` | FACULTY/ADMIN |
| Browse clubs | Clubs | Club | `GET /api/clubs` | Public |
| Club detail | Clubs | Club, ClubLead, ClubAchievement | `GET /api/clubs/{slug}` | Public |
| Join/leave club | Clubs | ClubMembership | `POST/DELETE /api/clubs/{slug}/join` | Auth |
| Announcements feed | Announcements | Announcement | `GET /api/announcements` | Public |
| Create announcement | Announcements | Announcement | `POST /api/announcements` | FACULTY/ADMIN |
| Learning Hub browser | Learning Hub | LearningMaterial, Subject | `GET /api/materials` | Public |
| Material reader | Learning Hub | LearningMaterial | `GET /api/materials/key/{key}` | Public |
| Bookmark material | Learning Hub | MaterialProgress | `POST /api/materials/{id}/bookmark` | Auth |
| Track reading progress | Learning Hub | MaterialProgress | `PATCH /api/materials/{id}/progress` | Auth |
| AI Doubt Panel | AI | AiConversation, AiMessage | `POST /api/ai/ask-doubt` | Auth |
| Campus AI Chatbot | AI | AiConversation, AiMessage | `POST /api/ai/chat` | Auth |
| Faculty Skill Hub | Skill Sessions | SkillSession | `GET /api/sessions` | Public |
| Enroll in session | Skill Sessions | SessionEnrollment | `POST /api/sessions/{slug}/enroll` | Auth |
| Mentor discovery | Mentorship | MentorProfile, StudentProfile | `GET /api/mentors` | Auth |
| Request mentorship | Mentorship | MentorshipRequest | `POST /api/mentors/request` | Auth |
| Student profile | Profile | User, StudentProfile, StudentSkill | `GET /api/profile/student` | STUDENT |
| Faculty profile | Profile | User, FacultyProfile | `GET /api/profile/faculty` | FACULTY |
| Update profile | Profile | User, StudentProfile | `PUT /api/profile/student` | STUDENT |
| Platform feedback | Feedback | feedback_platform | `POST /api/feedback/platform` | Auth |
| Faculty feedback | Feedback | feedback_faculty | `POST /api/feedback/faculty` | Auth |
| Campus navigation | Navigation | CampusPlace | `GET /api/places` | Public |
| Notifications | Notifications | Notification | `GET /api/notifications` | Auth |
| Mark notification read | Notifications | Notification | `PATCH /api/notifications/{id}/read` | Auth |

---

## D. Authentication Mapping

| Flow | Frontend | Backend |
|---|---|---|
| Login | `signInWithPassword(role, email, password)` in `auth-mock.ts` | `POST /api/auth/login` → JWT |
| Google OAuth | `signInWithGoogle(role)` placeholder | `POST /api/auth/google` *(future)* |
| Logout | Discard token client-side | `POST /api/auth/logout` (stateless — client discards JWT) |
| Current user | N/A (mock) | `GET /api/auth/me` |
| Token claim | N/A | `{ sub: userId, role, name, email }` |

**User roles:** `STUDENT`, `FACULTY`, `ADMIN`

**Protected routes:** All `/app/*` routes require a valid JWT `Authorization: Bearer <token>` header.

**Public routes:** `GET /api/events`, `GET /api/clubs`, `GET /api/announcements`, `GET /api/places`, `GET /api/sessions`, `GET /api/subjects`, all `/api/auth/*`.

---

## E. Learning Hub Mapping

| Frontend Filter | Backend Parameter |
|---|---|
| Semester tabs (1–8) | `?semester=6` |
| Subject dropdown | `?subject=CS601` |
| Material type filter | `?type=NOTES` |
| Search box | `?q=machine+learning` |
| Bookmarked toggle | `GET /api/materials/bookmarked` |

**Material ID format:** Frontend uses `m-{semester}-{subjectCode}-{materialType}` (e.g. `m-6-CS601-NOTES`).  
Backend stores this as `material_key` and exposes `GET /api/materials/key/{materialKey}`.

**AI Doubt Panel request:**
```json
POST /api/ai/ask-doubt
{
  "conversationId": "uuid-or-null",
  "question": "What is backpropagation?",
  "documentTitle": "Machine Learning Notes",
  "documentContext": "...(extracted text)...",
  "currentSection": "Chapter 3: Neural Networks",
  "documentId": "m-6-CS601-NOTES",
  "history": []
}
```

---

## F. AI Mapping

| Frontend AI Feature | Endpoint | Request | Response |
|---|---|---|---|
| AI Doubt Panel (material reader) | `POST /api/ai/ask-doubt` | `AskDoubtRequest` | `{ answer, conversationId, demo }` |
| General Campus AI Chat | `POST /api/ai/chat` | `AiChatRequest` | `{ reply, conversationId, demo }` |
| Conversation history | `GET /api/ai/conversations` | — | `PageResponse<ConversationResponse>` |
| Get conversation | `GET /api/ai/conversations/{id}` | — | `ConversationResponse` |

**Current mode:** Demo (no real LLM). All responses include `"demo": true`.

**Future RAG path:**
```
Document PDF → Text extraction → Chunking → Embeddings
→ Vector DB → Retriever → Relevant chunks + Question → LLM → Answer + sources
```

To enable: implement `RealAiServiceImpl implements AiService` and annotate `@Primary`.
