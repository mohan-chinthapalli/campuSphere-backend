# CampuSphere Backend — Complete API Reference

> **Single source of truth for Postman testing and frontend integration.**
>
> Base URL: `http://localhost:8080`
> All responses use the envelope: `{ "success": bool, "message": "...", "data": {...}, "timestamp": "..." }`
> Authentication: `Authorization: Bearer <JWT>`

---

## Postman Environment Variables

```
baseUrl          = http://localhost:8080
studentToken     = (set automatically by login test)
facultyToken     = (set automatically by login test)
adminToken       = (set automatically by login test)
studentId        = 2
facultyId        = 3
adminId          = 1
eventSlug        = hackspire-2026
clubSlug         = coding-club
materialId       = 1
materialKey      = m-6-CS601-NOTES
sessionSlug      = applied-ai
mentorUserId     = 10
announcementId   = 1
notificationId   = 1
conversationId   = (set after first AI call)
```

---

## Testing Order (Recommended Sequence)

```
STEP 01  → POST /api/auth/login         (student)   → save studentToken
STEP 02  → POST /api/auth/login         (faculty)   → save facultyToken
STEP 03  → POST /api/auth/login         (admin)     → save adminToken
STEP 04  → GET  /api/auth/me            (student)   → verify identity
STEP 05  → GET  /api/events             (public)    → list events
STEP 06  → GET  /api/events/hackspire-2026          → event detail
STEP 07  → POST /api/events/hackspire-2026/register (student) → register
STEP 08  → POST /api/events/hackspire-2026/register (student) → expect 409
STEP 09  → DELETE /api/events/hackspire-2026/register (student) → unregister
STEP 10  → GET  /api/clubs              (public)    → list clubs
STEP 11  → POST /api/clubs/coding-club/join         (student) → join
STEP 12  → POST /api/clubs/coding-club/join         (student) → expect 409
STEP 13  → GET  /api/materials?semester=6           → materials
STEP 14  → GET  /api/materials/key/m-6-CS601-NOTES  → single material
STEP 15  → POST /api/materials/1/bookmark           (student) → toggle
STEP 16  → PATCH /api/materials/1/progress          (student) → update
STEP 17  → GET  /api/materials/bookmarked           (student)
STEP 18  → GET  /api/subjects?semester=6            → subjects
STEP 19  → GET  /api/sessions                       → skill sessions
STEP 20  → POST /api/sessions/applied-ai/enroll     (student)
STEP 21  → GET  /api/academics/dashboard            (student)
STEP 22  → GET  /api/academics/timetable            (student)
STEP 23  → GET  /api/academics/deadlines            (student)
STEP 24  → GET  /api/profile            (student)   → role-aware
STEP 25  → PUT  /api/profile/student    (student)   → update
STEP 26  → GET  /api/announcements      (public)
STEP 27  → POST /api/announcements      (faculty)   → create
STEP 28  → DELETE /api/announcements/1  (admin)
STEP 29  → GET  /api/notifications      (student)
STEP 30  → GET  /api/notifications/unread-count
STEP 31  → PATCH /api/notifications/1/read
STEP 32  → PATCH /api/notifications/read-all
STEP 33  → GET  /api/mentors            (student)
STEP 34  → POST /api/mentors/request    (student)
STEP 35  → POST /api/ai/chat            (student)   → save conversationId
STEP 36  → POST /api/ai/ask-doubt       (student)
STEP 37  → GET  /api/ai/conversations   (student)
STEP 38  → POST /api/feedback/platform  (student)
STEP 39  → POST /api/feedback/faculty   (student)
STEP 40  → GET  /api/places             (public)

Security Tests:
STEP 41  → GET  /api/academics/dashboard  (no token)   → expect 401
STEP 42  → POST /api/events  (studentToken)             → expect 403
STEP 43  → DELETE /api/announcements/1 (facultyToken)  → expect 403
STEP 44  → GET  /api/profile/student  (facultyToken)   → expect 403
```

---

## MODULE 01 — Authentication

### POST /api/auth/register
Register a new user account.

- **Auth:** None
- **Role:** Public
- **Headers:** `Content-Type: application/json`

**Request Body:**
```json
{
  "name": "Test Student",
  "email": "test@campusphere.edu",
  "password": "Test@1234",
  "role": "STUDENT"
}
```
*`role` values: `STUDENT`, `FACULTY`, `ADMIN`*
*Password must contain uppercase, lowercase, and a digit — minimum 8 characters*

**Response 201:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGc...",
    "role": "STUDENT",
    "name": "Test Student",
    "email": "test@campusphere.edu",
    "userId": 11
  }
}
```

**Error Responses:**
| Status | errorCode | Trigger |
|---|---|---|
| 400 | VALIDATION_ERROR | Missing/invalid fields |
| 409 | DUPLICATE_RESOURCE | Email already registered |

**Postman Test Script:**
```javascript
if (pm.response.code === 201) {
    pm.environment.set("studentToken", pm.response.json().data.token);
}
pm.test("Status is 201", () => pm.response.to.have.status(201));
pm.test("Token present", () => pm.expect(pm.response.json().data.token).to.not.be.empty);
```

---

### POST /api/auth/login
Login with email and password.

- **Auth:** None
- **Role:** Public

**Request Body:**
```json
{
  "email": "student@campusphere.edu",
  "password": "Demo@1234"
}
```

**Response 200:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGc...",
    "role": "STUDENT",
    "name": "Aarav Sharma",
    "email": "student@campusphere.edu",
    "userId": 2
  }
}
```

**Error Responses:**
| Status | errorCode | Trigger |
|---|---|---|
| 400 | VALIDATION_ERROR | Missing email or password |
| 401 | INVALID_CREDENTIALS | Wrong email or password |

**Postman Test Script:**
```javascript
if (pm.response.code === 200) {
    const data = pm.response.json().data;
    if (data.role === "STUDENT") pm.environment.set("studentToken", data.token);
    if (data.role === "FACULTY") pm.environment.set("facultyToken", data.token);
    if (data.role === "ADMIN")   pm.environment.set("adminToken", data.token);
}
pm.test("Login successful", () => pm.response.to.have.status(200));
pm.test("Token received", () => pm.expect(pm.response.json().data.token).to.be.a("string"));
```

---

### GET /api/auth/me
Get the currently authenticated user.

- **Auth:** Required
- **Role:** Any authenticated

**Headers:** `Authorization: Bearer {{studentToken}}`

**Response 200:**
```json
{
  "success": true,
  "data": {
    "id": 2,
    "name": "Aarav Sharma",
    "email": "student@campusphere.edu",
    "role": "STUDENT",
    "active": true,
    "createdAt": "2026-08-20T00:00:00Z"
  }
}
```

**Error Responses:**
| Status | errorCode | Trigger |
|---|---|---|
| 401 | UNAUTHORIZED | No/invalid token |

---

### POST /api/auth/logout
Logout (stateless — client discards token).

- **Auth:** Required
- **Role:** Any

**Response 200:**
```json
{ "success": true, "message": "Logged out successfully" }
```

---

## MODULE 02 — Events

### GET /api/events
List and search events with pagination.

- **Auth:** Optional (provides `registeredByCurrentUser` when authenticated)
- **Role:** Public

**Query Parameters:**
| Param | Type | Description |
|---|---|---|
| `category` | String | Filter: Hackathon, Cultural, Tech Talk, etc. |
| `q` | String | Search title/tagline/venue |
| `page` | int | Page number (default: 0) |
| `size` | int | Page size (default: 10) |

**Headers (optional):** `Authorization: Bearer {{studentToken}}`

**Response 200:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1, "slug": "hackspire-2026", "title": "HackSpire 2026",
        "category": "Hackathon", "tagline": "Build the future in 24 hours",
        "eventDate": "Aug 07, 2026", "venue": "Main Auditorium + Labs",
        "seatsTotal": 200, "seatsLeft": 194, "participants": 6,
        "gradient": "from-violet-500 to-purple-600", "emoji": "💻",
        "registeredByCurrentUser": false,
        "organizer": { "name": "Aarav Sharma", "club": "Coding Club", "email": "coding@campusphere.edu" },
        "agenda": [ { "time": "09:00 AM", "title": "Registration & Check-in" } ]
      }
    ],
    "page": 0, "size": 10, "totalElements": 6, "totalPages": 1, "last": true
  }
}
```

---

### GET /api/events/{slug}
Get a single event by slug.

- **Auth:** Optional
- **Role:** Public

**Path:** `{{baseUrl}}/api/events/hackspire-2026`

**Response 200:** Full `EventResponse` including `agenda`, `organizer`, `registeredByCurrentUser`

**Error Responses:**
| Status | errorCode |
|---|---|
| 404 | RESOURCE_NOT_FOUND |

---

### POST /api/events
Create a new event.

- **Auth:** Required
- **Role:** FACULTY, ADMIN
- **Headers:** `Authorization: Bearer {{facultyToken}}`

**Request Body:**
```json
{
  "title": "AI Workshop 2026",
  "category": "Workshop",
  "tagline": "Hands-on ML session",
  "description": "Learn ML fundamentals hands-on.",
  "venue": "Lab Block C",
  "seatsTotal": 50,
  "startsAt": "2026-09-20T09:00:00Z",
  "eventDate": "Sep 20, 2026",
  "displayTime": "09:00 AM – 05:00 PM",
  "organizerName": "Dr. Priya Menon",
  "organizerClub": "AI Research Club",
  "organizerEmail": "ai@campusphere.edu",
  "tags": ["ai", "workshop", "ml"],
  "agenda": [
    { "time": "09:00 AM", "title": "Introduction to ML" },
    { "time": "11:00 AM", "title": "Hands-on Lab" }
  ]
}
```

**Response 201:** Full `EventResponse`

**Error Responses:**
| Status | errorCode |
|---|---|
| 400 | VALIDATION_ERROR |
| 401 | UNAUTHORIZED |
| 403 | FORBIDDEN |

---

### POST /api/events/{slug}/register
Register the current user for an event.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Response 200:** Updated `EventResponse` with `registeredByCurrentUser: true`

**Error Responses:**
| Status | errorCode |
|---|---|
| 404 | RESOURCE_NOT_FOUND |
| 409 | DUPLICATE_RESOURCE |
| 400 | BAD_REQUEST (event full) |

---

### DELETE /api/events/{slug}/register
Cancel event registration.

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** `{ "success": true, "message": "Registration cancelled" }`

---

### DELETE /api/events/{slug}
Delete an event (creator or ADMIN only).

- **Auth:** Required
- **Role:** FACULTY (own events), ADMIN
- **Headers:** `Authorization: Bearer {{facultyToken}}`

**Response 200:** `{ "success": true, "message": "Event deleted" }`

**Error Responses:**
| Status | errorCode |
|---|---|
| 403 | FORBIDDEN (not creator) |
| 404 | RESOURCE_NOT_FOUND |

---

## MODULE 03 — Clubs

### GET /api/clubs
List and search clubs.

- **Auth:** Optional
- **Role:** Public

**Query Parameters:** `category`, `q`, `page`, `size`

**Response 200:** `PageResponse<ClubResponse>` including `leads`, `achievements`, `memberOfCurrentUser`

---

### GET /api/clubs/{slug}
Get club detail.

- **Auth:** Optional
- **Role:** Public

**Response 200:** Full `ClubResponse`

---

### POST /api/clubs/{slug}/join
Join a club.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Response 200:** Updated `ClubResponse` with `memberOfCurrentUser: true`

**Error Responses:**
| Status | errorCode |
|---|---|
| 409 | DUPLICATE_RESOURCE |

---

### DELETE /api/clubs/{slug}/join  *(or POST /api/clubs/{slug}/leave)*
Leave a club.

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** Updated `ClubResponse`

**Error Responses:**
| Status | errorCode |
|---|---|
| 400 | BAD_REQUEST (not a member) |

---

## MODULE 04 — Announcements

### GET /api/announcements
List announcements.

- **Auth:** Optional
- **Role:** Public

**Query Parameters:**
| Param | Values |
|---|---|
| `tag` | ACADEMICS, CAMPUS, PLACEMENTS, RESEARCH, EVENTS, GENERAL |
| `q` | search string |
| `page`, `size` | pagination |

**Response 200:** `PageResponse<AnnouncementResponse>`

---

### GET /api/announcements/{id}
Get single announcement.

- **Auth:** Optional
- **Role:** Public

**Response 200:** `AnnouncementResponse`

---

### POST /api/announcements
Create announcement.

- **Auth:** Required
- **Role:** FACULTY, ADMIN
- **Headers:** `Authorization: Bearer {{facultyToken}}`

**Request Body:**
```json
{
  "title": "Exam Schedule Released",
  "body": "The semester 6 exam schedule is now available.",
  "author": "Examination Cell",
  "tag": "ACADEMICS",
  "priority": "HIGH"
}
```

**Response 201:** `AnnouncementResponse`

---

### DELETE /api/announcements/{id}
Delete announcement.

- **Auth:** Required
- **Role:** ADMIN only
- **Headers:** `Authorization: Bearer {{adminToken}}`

**Response 200:** `{ "success": true }`

---

## MODULE 05 — Learning Hub

### GET /api/subjects
List subjects, optionally filtered by semester.

- **Auth:** Optional
- **Role:** Public

**Query Parameters:** `semester` (1–8)

**Response 200:**
```json
{
  "success": true,
  "data": [
    { "id": 21, "code": "CS601", "name": "Machine Learning", "emoji": "🤖", "semester": 6, "facultyName": "Dr. Priya Menon" }
  ]
}
```

---

### GET /api/materials
Search learning materials.

- **Auth:** Optional (includes user progress when authenticated)
- **Role:** Public

**Query Parameters:**
| Param | Description |
|---|---|
| `semester` | 1–8 |
| `subject` | Subject code e.g. `CS601` |
| `type` | NOTES, PDF, VIDEO, PAPER, HANDWRITTEN, LAB_MANUAL, SYLLABUS |
| `q` | Search title/author/subject |
| `page`, `size` | Pagination |

**Response 200:** `PageResponse<MaterialResponse>` (includes `progressPct`, `bookmarked` when authenticated)

---

### GET /api/materials/{id}
Get material by numeric ID.

- **Auth:** Optional
- **Role:** Public

---

### GET /api/materials/key/{materialKey}
Get material by frontend key format `m-{semester}-{subjectCode}-{type}`.

- **Auth:** Optional
- **Role:** Public

**Example:** `GET /api/materials/key/m-6-CS601-NOTES`

**Response 200:** `MaterialResponse`

---

### GET /api/materials/bookmarked
Get current user's bookmarked materials.

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** `List<MaterialResponse>`

---

### PATCH /api/materials/{id}/progress
Update reading progress and/or bookmark.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Request Body:**
```json
{ "progressPct": 65, "bookmarked": true }
```

**Response 200:** Updated `MaterialResponse`

---

### POST /api/materials/{id}/bookmark
Toggle bookmark on a material.

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** Updated `MaterialResponse`

---

### POST /api/materials/{id}/download
Record a download (increments download counter).

- **Auth:** Optional

**Response 200:** Updated `MaterialResponse`

---

## MODULE 06 — Skill Sessions (Faculty Skill Hub)

### GET /api/sessions
List/search skill sessions.

- **Auth:** Optional
- **Role:** Public

**Query Parameters:** `category`, `q`, `page`, `size`

**Response 200:** `PageResponse<SkillSessionResponse>` (includes `enrolledByCurrentUser`, `completedSessions` when authenticated)

---

### GET /api/sessions/{slug}
Get skill session detail.

- **Auth:** Optional
- **Role:** Public

**Response 200:** Full `SkillSessionResponse` with `outcomes`

---

### POST /api/sessions/{slug}/enroll
Enroll in a skill session.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Response 200:** Updated `SkillSessionResponse` with `enrolledByCurrentUser: true`

**Error Responses:**
| Status | errorCode |
|---|---|
| 409 | DUPLICATE_RESOURCE |

---

### DELETE /api/sessions/{slug}/enroll
Unenroll from a session.

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** `{ "success": true }`

---

## MODULE 07 — Mentorship

### GET /api/mentors
List/search mentors.

- **Auth:** Required
- **Role:** Any authenticated

**Query Parameters:** `q`, `skill`, `branch`, `page`, `size`

**Response 200:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "userId": 10, "name": "Vikram Singh", "year": "Final Year · Semester 8",
        "branch": "Computer Science and Engineering",
        "headline": "Final Year CS | Full-Stack Dev & AI Enthusiast",
        "skills": ["Node.js", "React", "MongoDB"],
        "avgRating": 4.9, "totalSessions": 24,
        "responseTime": "~2h", "available": true
      }
    ]
  }
}
```

---

### GET /api/mentors/{userId}
Get mentor profile by user ID.

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** `MentorResponse`

---

### POST /api/mentors/request
Send a mentorship request.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Request Body:**
```json
{
  "mentorUserId": 10,
  "message": "I need help with React and system design."
}
```

**Response 200:** `{ "success": true, "message": "Mentorship request sent" }`

**Error Responses:**
| Status | errorCode |
|---|---|
| 400 | BAD_REQUEST (not a registered mentor) |
| 409 | DUPLICATE_RESOURCE (pending request exists) |

---

### PATCH /api/mentors/requests/{requestId}/respond
Accept or reject a mentorship request (mentor only).

- **Auth:** Required
- **Role:** The mentor user only

**Query Parameters:** `action=ACCEPT` or `action=REJECT`

**Response 200:** `{ "success": true, "message": "Response recorded" }`

**Error Responses:**
| Status | errorCode |
|---|---|
| 403 | FORBIDDEN (not the mentor) |
| 400 | BAD_REQUEST (already responded) |

---

## MODULE 08 — Academics

### GET /api/academics/dashboard
Full student dashboard data.

- **Auth:** Required
- **Role:** STUDENT only
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Response 200:**
```json
{
  "success": true,
  "data": {
    "student": {
      "name": "Aarav Sharma", "initials": "AS", "rollNumber": "CS22B1043",
      "branch": "Computer Science and Engineering", "year": "Third Year · Semester 6",
      "cgpa": 8.74, "attendance": 87.5, "credits": 162
    },
    "todayClasses": [
      { "courseCode": "CS601", "courseName": "Machine Learning", "classTime": "09:00 AM", "room": "A-301", "facultyName": "Dr. Priya Menon" }
    ],
    "upcomingDeadlines": [
      { "title": "ML Assignment 3", "courseCode": "CS601", "dueAt": "2026-08-20T23:59:00Z", "urgency": "HIGH" }
    ],
    "upcomingEvents": [...],
    "recentNotifications": [...],
    "unreadNotifications": 3
  }
}
```

**Error Responses:**
| Status | errorCode |
|---|---|
| 403 | FORBIDDEN (not STUDENT role) |

---

### GET /api/academics/timetable
Student's full weekly timetable.

- **Auth:** Required
- **Role:** STUDENT only

**Response 200:** `List<TimetableResponse>`

---

### GET /api/academics/deadlines
Student's upcoming deadlines.

- **Auth:** Required
- **Role:** STUDENT only

**Response 200:** `List<DeadlineResponse>`

---

## MODULE 09 — Profile

### GET /api/profile
Get profile of currently authenticated user (role-aware).

- **Auth:** Required
- **Role:** Any authenticated

Returns `StudentProfileResponse` for STUDENT, `FacultyProfileResponse` for FACULTY, `UserResponse` for ADMIN.

**Response 200 (STUDENT):**
```json
{
  "success": true,
  "data": {
    "userId": 2, "name": "Aarav Sharma", "email": "student@campusphere.edu",
    "rollNumber": "CS22B1043", "branch": "Computer Science and Engineering",
    "year": "Third Year · Semester 6", "semester": 6,
    "cgpa": 8.74, "attendance": 87.5, "credits": 162,
    "bio": "Passionate about AI/ML...",
    "skills": ["React", "Spring Boot", "Python", "SQL", "Docker"],
    "initials": "AS"
  }
}
```

---

### GET /api/profile/student
Get current student profile.

- **Auth:** Required
- **Role:** STUDENT only

---

### PUT /api/profile/student
Update student profile (name, bio, skills).

- **Auth:** Required
- **Role:** STUDENT only

**Request Body:**
```json
{
  "name": "Aarav Sharma",
  "bio": "Updated bio here.",
  "skills": ["React", "Spring Boot", "Python", "Docker", "Kubernetes"]
}
```

**Response 200:** Updated `StudentProfileResponse`

---

### GET /api/profile/faculty
Get current faculty profile.

- **Auth:** Required
- **Role:** FACULTY only

---

### PUT /api/profile/faculty
Update faculty profile.

- **Auth:** Required
- **Role:** FACULTY only

**Request Body:**
```json
{
  "name": "Dr. Priya Menon",
  "bio": "Updated research interests.",
  "office": "Block A, Room 201",
  "officeHours": "Mon-Fri 2PM-5PM"
}
```

---

### GET /api/profile/faculty/{userId}
Get a faculty profile by user ID (for students to view their faculty).

- **Auth:** Required
- **Role:** Any authenticated

**Response 200:** `FacultyProfileResponse`

---

## MODULE 10 — Notifications

### GET /api/notifications
List notifications for current user.

- **Auth:** Required
- **Role:** Any authenticated

**Query Parameters:** `page`, `size`

**Response 200:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1, "title": "HackSpire 2026 Registration Open",
        "body": "Registration is now open...",
        "notificationType": "EVENT",
        "read": false, "createdAt": "2026-08-20T..."
      }
    ],
    "page": 0, "size": 20, "totalElements": 5
  }
}
```

---

### GET /api/notifications/unread-count
Get count of unread notifications.

- **Auth:** Required

**Response 200:** `{ "success": true, "data": { "unreadCount": 3 } }`

---

### PATCH /api/notifications/{id}/read
Mark single notification as read.

- **Auth:** Required

**Response 200:** Updated `NotificationResponse`

**Error Responses:**
| Status | errorCode |
|---|---|
| 403 | FORBIDDEN (not owner) |
| 404 | RESOURCE_NOT_FOUND |

---

### PATCH /api/notifications/read-all
Mark all notifications as read.

- **Auth:** Required

**Response 200:** `{ "success": true, "data": { "updatedCount": 5 } }`

---

### DELETE /api/notifications/{id}
Delete a notification.

- **Auth:** Required

**Response 200:** `{ "success": true }`

---

## MODULE 11 — Feedback

### POST /api/feedback/platform
Submit platform feedback.

- **Auth:** Required
- **Role:** Any authenticated

**Request Body:**
```json
{
  "topic": "Learning Hub",
  "rating": 5,
  "subject": "UI Design",
  "details": "The interface is very intuitive and easy to use."
}
```

**Response 200:** `{ "success": true, "message": "Feedback submitted — thank you!" }`

---

### POST /api/feedback/faculty
Submit anonymous faculty feedback.

- **Auth:** Required
- **Role:** Any authenticated

**Request Body:**
```json
{
  "facultyUserId": 3,
  "courseCode": "CS601",
  "semester": 6,
  "clarityScore": 5,
  "paceScore": 4,
  "supportScore": 5,
  "fairnessScore": 5,
  "comments": "Excellent teaching methodology."
}
```

> ⚠️ **Feedback is anonymous** — student ID is never stored.

**Response 200:** `{ "success": true, "message": "Faculty feedback submitted anonymously" }`

**Error Responses:**
| Status | errorCode |
|---|---|
| 400 | VALIDATION_ERROR (scores out of 1–5 range) |
| 404 | RESOURCE_NOT_FOUND (faculty user doesn't exist) |

---

## MODULE 12 — Campus Navigation

### GET /api/places
List/search campus places.

- **Auth:** Optional
- **Role:** Public

**Query Parameters:** `q` (search name or type)

**Response 200:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1, "slug": "block-a", "name": "Block A (Academic)",
      "type": "Academic", "floors": 4, "openHours": "8AM–8PM",
      "mapX": 30.0, "mapY": 25.0, "walkTime": "2 min"
    }
  ]
}
```

---

### GET /api/places/{slug}
Get a campus place by slug.

- **Auth:** Optional
- **Role:** Public

**Example:** `GET /api/places/library`

---

## MODULE 13 — AI Assistant

### POST /api/ai/ask-doubt
AI Doubt Panel — used inside the Learning Hub material reader.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Request Body:**
```json
{
  "conversationId": null,
  "question": "What is backpropagation and how does it work?",
  "documentTitle": "Machine Learning Notes",
  "documentContext": "Chapter 3: Neural Networks. Backpropagation is...",
  "currentSection": "Chapter 3: Neural Networks",
  "documentId": "m-6-CS601-NOTES",
  "history": []
}
```
*`conversationId`: Pass `null` to start new conversation. Pass existing UUID to continue.*

**Response 200:**
```json
{
  "success": true,
  "data": {
    "conversationId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "answer": "Great question! Based on **Machine Learning Notes**...",
    "reply": "Great question! Based on **Machine Learning Notes**...",
    "sources": [],
    "demo": true
  }
}
```

**Postman Test Script:**
```javascript
if (pm.response.code === 200) {
    pm.environment.set("conversationId", pm.response.json().data.conversationId);
}
```

---

### POST /api/ai/chat
General Campus AI Chatbot.

- **Auth:** Required
- **Role:** Any authenticated
- **Headers:** `Authorization: Bearer {{studentToken}}`

**Request Body:**
```json
{
  "conversationId": null,
  "message": "Where is the central library?"
}
```

**Response 200:**
```json
{
  "success": true,
  "data": {
    "conversationId": "uuid",
    "answer": "🗺️ **Campus Navigation:** Central Library is open 8AM–10PM...",
    "reply": "🗺️ **Campus Navigation:** Central Library is open 8AM–10PM...",
    "sources": [],
    "demo": true
  }
}
```

---

### GET /api/ai/conversations
List all AI conversations for current user.

- **Auth:** Required

**Query Parameters:** `page`, `size`

**Response 200:** `PageResponse<ConversationResponse>`

---

### GET /api/ai/conversations/{conversationId}
Get full conversation history.

- **Auth:** Required (owner only)

**Response 200:**
```json
{
  "success": true,
  "data": {
    "conversationId": "uuid",
    "title": "Machine Learning Notes",
    "conversationType": "DOUBT",
    "messages": [
      { "id": 1, "role": "USER", "content": "What is backpropagation?", "demo": true, "createdAt": "..." },
      { "id": 2, "role": "ASSISTANT", "content": "Great question! ...", "demo": true, "createdAt": "..." }
    ]
  }
}
```

**Error Responses:**
| Status | errorCode |
|---|---|
| 403 | FORBIDDEN (not owner) |
| 404 | RESOURCE_NOT_FOUND |

---

## Error Response Format

All error responses follow this structure:
```json
{
  "success": false,
  "message": "Human-readable error description",
  "errorCode": "MACHINE_READABLE_CODE",
  "path": "/api/path/that/failed",
  "timestamp": "2026-08-20T21:00:00Z"
}
```

### Error Codes Reference
| errorCode | HTTP Status | Description |
|---|---|---|
| `INVALID_CREDENTIALS` | 401 | Wrong email or password |
| `UNAUTHORIZED` | 401 | No JWT or invalid JWT |
| `FORBIDDEN` | 403 | Authenticated but insufficient role/ownership |
| `RESOURCE_NOT_FOUND` | 404 | Entity does not exist |
| `DUPLICATE_RESOURCE` | 409 | Constraint violation (already registered, already member, etc.) |
| `BAD_REQUEST` | 400 | Invalid business operation |
| `VALIDATION_ERROR` | 400 | Bean validation failure |
| `MALFORMED_REQUEST` | 400 | Malformed/unparseable JSON body |
| `METHOD_NOT_ALLOWED` | 405 | Wrong HTTP method |
| `MISSING_PARAMETER` | 400 | Required query param absent |
| `TYPE_MISMATCH` | 400 | Wrong type for path/query param |
| `USE_SPECIFIC_ENDPOINT` | 200 | Redirect hint for generic endpoints |
| `INTERNAL_ERROR` | 500 | Unexpected server error |

---

## Security Testing Checklist

### Test 1 — No Token
```
GET /api/academics/dashboard
Authorization: (none)
Expected: 401 UNAUTHORIZED
```

### Test 2 — Invalid Token
```
GET /api/academics/dashboard
Authorization: Bearer invalid.token.here
Expected: 401 UNAUTHORIZED
```

### Test 3 — Student Accessing Faculty Endpoint
```
POST /api/events
Authorization: Bearer {{studentToken}}
Body: { "title": "Test", "category": "Workshop", "venue": "Lab", "seatsTotal": 10 }
Expected: 403 FORBIDDEN
```

### Test 4 — Student Accessing Admin Endpoint
```
DELETE /api/announcements/1
Authorization: Bearer {{studentToken}}
Expected: 403 FORBIDDEN
```

### Test 5 — Faculty Accessing Admin Endpoint
```
DELETE /api/announcements/1
Authorization: Bearer {{facultyToken}}
Expected: 403 FORBIDDEN
```

### Test 6 — IDOR — Student Accessing Another Student's Notifications
```
PATCH /api/notifications/1/read
Authorization: Bearer {{studentToken2}}  (a different student who doesn't own notification id=1)
Expected: 403 FORBIDDEN
```

### Test 7 — Duplicate Registration
```
POST /api/events/hackspire-2026/register  (twice)
Authorization: Bearer {{studentToken}}
First:  200 OK
Second: 409 CONFLICT — DUPLICATE_RESOURCE
```

### Test 8 — Validation Error
```
POST /api/auth/login
Body: { "email": "not-an-email", "password": "" }
Expected: 400 VALIDATION_ERROR
```

### Test 9 — Malformed JSON
```
POST /api/auth/login
Content-Type: application/json
Body: { invalid json here
Expected: 400 MALFORMED_REQUEST
```

### Test 10 — SQL Injection Attempt
```
GET /api/events?q='; DROP TABLE events; --
Expected: 200 OK (empty results — JPA parameterized queries prevent injection)
```

---

## Frontend Integration Guide

### Base URL
```javascript
const API_BASE = 'http://localhost:8080';
```

### Authorization Header
```javascript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${localStorage.getItem('campusphere_token')}`
};
```

### Token Storage (frontend side)
```javascript
// After login:
const res = await fetch(`${API_BASE}/api/auth/login`, { method: 'POST', ... });
const { data } = await res.json();
localStorage.setItem('campusphere_token', data.token);
localStorage.setItem('campusphere_role', data.role);
localStorage.setItem('campusphere_user', JSON.stringify({ name: data.name, email: data.email, userId: data.userId }));
```

### Error Handling (frontend side)
```javascript
async function apiCall(url, options = {}) {
  const res = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('campusphere_token')}`,
      ...options.headers
    }
  });
  const body = await res.json();
  if (!body.success) {
    if (res.status === 401) { /* redirect to login */ }
    if (res.status === 403) { /* show access denied */ }
    throw new Error(body.message);
  }
  return body.data;
}
```

### CORS
The backend is pre-configured to accept requests from:
- `http://localhost:3000`
- `http://localhost:5173`

To add a different origin, set `FRONTEND_URL=http://your-origin` in the environment.

### Replacing Mock Data
The frontend currently uses `src/lib/data.ts` for static data. For each module, replace with an API call:

```typescript
// Example: Replace mock events with real API
// Before (mock):
const events = MOCK_EVENTS;

// After (real API):
const events = await apiCall('/api/events?page=0&size=20');

// Example: Replace auth-mock.ts
// Before:
signInWithPassword(role, email, password)

// After:
const data = await apiCall('/api/auth/login', {
  method: 'POST',
  body: JSON.stringify({ email, password })
});
// data = { token, role, name, email, userId }
```

---

## Swagger UI

Live API documentation available at:
```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:
```
http://localhost:8080/api-docs
```

All endpoints are documented with request/response schemas and support Try-it-out testing with a Bearer token.
