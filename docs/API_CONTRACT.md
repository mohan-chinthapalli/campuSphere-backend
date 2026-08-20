# CampuSphere — API Contract

Base URL: `http://localhost:8080`  
Auth: `Authorization: Bearer <JWT>`  
Content-Type: `application/json`

All responses use the envelope:
```json
{ "success": true, "message": "...", "data": { ... }, "timestamp": "..." }
```

---

## Authentication

### POST /api/auth/register
**Auth:** None  
**Body:**
```json
{ "name": "Aarav Sharma", "email": "s@campusphere.edu", "password": "Test@1234", "role": "STUDENT" }
```
**Response 201:**
```json
{ "token": "eyJ...", "role": "STUDENT", "name": "Aarav Sharma", "email": "...", "userId": 2 }
```

### POST /api/auth/login
**Auth:** None  
**Body:** `{ "email": "...", "password": "..." }`  
**Response 200:** Same as register  
**Response 401:** `{ "success": false, "errorCode": "INVALID_CREDENTIALS" }`

### GET /api/auth/me
**Auth:** Required  
**Response 200:** `{ "id": 2, "name": "...", "email": "...", "role": "STUDENT" }`

### POST /api/auth/logout
**Auth:** Required  
**Response 200:** `{ "success": true, "message": "Logged out successfully" }`

---

## Events

### GET /api/events
**Auth:** Optional  
**Query:** `?category=Hackathon&q=search&page=0&size=10`  
**Response 200:** `PageResponse<EventResponse>`

### GET /api/events/{slug}
**Auth:** Optional  
**Response 200:** `EventResponse` (includes `registeredByCurrentUser` if authenticated)  
**Response 404:** Event not found

### POST /api/events
**Auth:** FACULTY or ADMIN  
**Body:** `CreateEventRequest`  
**Response 201:** `EventResponse`

### POST /api/events/{slug}/register
**Auth:** Required  
**Response 200:** Updated `EventResponse`  
**Response 409:** Already registered  
**Response 400:** Event fully booked

### DELETE /api/events/{slug}/register
**Auth:** Required  
**Response 200:** `{ "success": true }`

### DELETE /api/events/{slug}
**Auth:** FACULTY or ADMIN (creator)  
**Response 200:** `{ "success": true }`

---

## Clubs

### GET /api/clubs
**Auth:** Optional  
**Query:** `?category=Technology&q=coding&page=0&size=10`  
**Response 200:** `PageResponse<ClubResponse>`

### GET /api/clubs/{slug}
**Auth:** Optional  
**Response 200:** `ClubResponse` (includes `memberOfCurrentUser`)

### POST /api/clubs/{slug}/join
**Auth:** Required  
**Response 200:** Updated `ClubResponse`  
**Response 409:** Already a member

### DELETE /api/clubs/{slug}/join
**Auth:** Required  
**Response 200:** Updated `ClubResponse`

---

## Announcements

### GET /api/announcements
**Auth:** Optional  
**Query:** `?tag=ACADEMICS&q=search&page=0&size=20`  
**Response 200:** `PageResponse<AnnouncementResponse>`

### GET /api/announcements/{id}
**Auth:** Optional  
**Response 200:** `AnnouncementResponse`

### POST /api/announcements
**Auth:** FACULTY or ADMIN  
**Body:** `{ "title": "...", "body": "...", "author": "...", "tag": "ACADEMICS", "priority": "HIGH" }`  
**Response 201:** `AnnouncementResponse`

---

## Learning Hub

### GET /api/subjects
**Auth:** Optional  
**Query:** `?semester=6`  
**Response 200:** `List<SubjectResponse>`

### GET /api/materials
**Auth:** Optional  
**Query:** `?semester=6&subject=CS601&type=NOTES&q=search&page=0&size=20`  
**Response 200:** `PageResponse<MaterialResponse>` (includes user progress if authenticated)

### GET /api/materials/{id}
**Auth:** Optional  
**Response 200:** `MaterialResponse`

### GET /api/materials/key/{materialKey}
**Auth:** Optional  
**Example:** `GET /api/materials/key/m-6-CS601-NOTES`  
**Response 200:** `MaterialResponse`

### GET /api/materials/bookmarked
**Auth:** Required  
**Response 200:** `List<MaterialResponse>`

### PATCH /api/materials/{id}/progress
**Auth:** Required  
**Body:** `{ "progressPct": 45, "bookmarked": true }`  
**Response 200:** `MaterialResponse`

### POST /api/materials/{id}/bookmark
**Auth:** Required  
**Response 200:** `MaterialResponse` (bookmarked toggled)

### POST /api/materials/{id}/download
**Auth:** Optional  
**Response 200:** `MaterialResponse` (downloadCount incremented)

---

## Skill Sessions

### GET /api/sessions
**Auth:** Optional  
**Query:** `?category=AI&q=search&page=0&size=10`  
**Response 200:** `PageResponse<SkillSessionResponse>`

### GET /api/sessions/{slug}
**Auth:** Optional  
**Response 200:** `SkillSessionResponse` (includes `enrolledByCurrentUser`)

### POST /api/sessions/{slug}/enroll
**Auth:** Required  
**Response 200:** Updated `SkillSessionResponse`  
**Response 409:** Already enrolled

### DELETE /api/sessions/{slug}/enroll
**Auth:** Required  
**Response 200:** `{ "success": true }`

---

## Mentorship

### GET /api/mentors
**Auth:** Optional  
**Query:** `?q=name&skill=React&branch=CS&page=0&size=10`  
**Response 200:** `PageResponse<MentorResponse>`

### GET /api/mentors/{userId}
**Auth:** Optional  
**Response 200:** `MentorResponse`

### POST /api/mentors/request
**Auth:** Required  
**Body:** `{ "mentorUserId": 10, "message": "I need help with React" }`  
**Response 200:** `{ "success": true }`

### PATCH /api/mentors/requests/{requestId}/respond
**Auth:** Required (mentor only)  
**Query:** `?action=ACCEPT` or `?action=REJECT`  
**Response 200:** `{ "success": true }`

---

## Academics

### GET /api/academics/dashboard
**Auth:** STUDENT  
**Response 200:** `DashboardResponse` (student summary, today's classes, deadlines, events, notifications)

### GET /api/academics/timetable
**Auth:** STUDENT  
**Response 200:** `List<TimetableResponse>`

### GET /api/academics/deadlines
**Auth:** STUDENT  
**Response 200:** `List<DeadlineResponse>`

---

## Profile

### GET /api/profile
**Auth:** Required (role-aware)  
**Response 200:** `StudentProfileResponse` or `FacultyProfileResponse`

### GET /api/profile/student
**Auth:** STUDENT  
**Response 200:** `StudentProfileResponse`

### PUT /api/profile/student
**Auth:** STUDENT  
**Body:** `{ "name": "...", "bio": "...", "skills": ["React", "Python"] }`  
**Response 200:** `StudentProfileResponse`

### GET /api/profile/faculty
**Auth:** FACULTY  
**Response 200:** `FacultyProfileResponse`

### PUT /api/profile/faculty
**Auth:** FACULTY  
**Body:** `{ "name": "...", "bio": "...", "office": "...", "officeHours": "..." }`  
**Response 200:** `FacultyProfileResponse`

### GET /api/profile/faculty/{userId}
**Auth:** Optional  
**Response 200:** `FacultyProfileResponse`

---

## Notifications

### GET /api/notifications
**Auth:** Required  
**Query:** `?page=0&size=20`  
**Response 200:** `PageResponse<NotificationResponse>`

### GET /api/notifications/unread-count
**Auth:** Required  
**Response 200:** `{ "unreadCount": 3 }`

### PATCH /api/notifications/{id}/read
**Auth:** Required  
**Response 200:** `NotificationResponse`

### PATCH /api/notifications/read-all
**Auth:** Required  
**Response 200:** `{ "updatedCount": 5 }`

### DELETE /api/notifications/{id}
**Auth:** Required  
**Response 200:** `{ "success": true }`

---

## Feedback

### POST /api/feedback/platform
**Auth:** Required  
**Body:** `{ "topic": "Learning Hub", "rating": 5, "subject": "UI", "details": "..." }`  
**Response 200:** `{ "success": true }`

### POST /api/feedback/faculty
**Auth:** Required  
**Body:** `{ "facultyUserId": 3, "courseCode": "CS601", "clarityScore": 5, "paceScore": 4, "supportScore": 5, "fairnessScore": 5, "comments": "..." }`  
**Response 200:** `{ "success": true }`  
Note: Faculty feedback is anonymous — student ID is not stored.

---

## Campus Navigation

### GET /api/places
**Auth:** Optional  
**Query:** `?q=library`  
**Response 200:** `List<CampusPlaceResponse>`

### GET /api/places/{slug}
**Auth:** Optional  
**Response 200:** `CampusPlaceResponse`

---

## AI Assistant

### POST /api/ai/ask-doubt
**Auth:** Required  
**Body:**
```json
{
  "conversationId": "uuid-or-null",
  "question": "What is backpropagation?",
  "documentTitle": "Machine Learning Notes",
  "documentContext": "...(page text)...",
  "currentSection": "Chapter 3",
  "documentId": "m-6-CS601-NOTES",
  "history": [
    { "role": "user", "content": "previous question" },
    { "role": "assistant", "content": "previous answer" }
  ]
}
```
**Response 200:**
```json
{
  "conversationId": "uuid",
  "answer": "Backpropagation is...",
  "reply": "Backpropagation is...",
  "sources": [],
  "demo": true
}
```

### POST /api/ai/chat
**Auth:** Required  
**Body:** `{ "conversationId": "uuid-or-null", "message": "Where is the library?" }`  
**Response 200:** Same structure as ask-doubt

### GET /api/ai/conversations
**Auth:** Required  
**Response 200:** `PageResponse<ConversationResponse>`

### GET /api/ai/conversations/{conversationId}
**Auth:** Required (own conversations only)  
**Response 200:** `ConversationResponse` with full message history

---

## Error Codes

| Code | HTTP Status | Meaning |
|---|---|---|
| `INVALID_CREDENTIALS` | 401 | Wrong email or password |
| `UNAUTHORIZED` | 401 | No/invalid JWT |
| `FORBIDDEN` | 403 | Insufficient role |
| `RESOURCE_NOT_FOUND` | 404 | Entity does not exist |
| `DUPLICATE_RESOURCE` | 409 | Already registered/joined |
| `BAD_REQUEST` | 400 | Invalid business operation |
| `VALIDATION_ERROR` | 400 | Bean validation failed |
| `INTERNAL_ERROR` | 500 | Unexpected server error |
