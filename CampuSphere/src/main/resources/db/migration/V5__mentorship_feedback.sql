-- ============================================================
-- V5: Mentorship, Feedback, Timetable, Deadlines
-- PostgreSQL-compatible (Supabase)
-- ============================================================

-- Mentor Profiles
CREATE TABLE mentor_profiles (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id         BIGINT          NOT NULL,
    headline        VARCHAR(500),
    available       BOOLEAN         NOT NULL DEFAULT TRUE,
    total_sessions  INT             NOT NULL DEFAULT 0,
    avg_rating      DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    response_time   VARCHAR(20)     NOT NULL DEFAULT '~24h',
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_mentor_profiles PRIMARY KEY (id),
    CONSTRAINT uk_mp_user UNIQUE (user_id),
    CONSTRAINT fk_mprof_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Mentorship Requests
CREATE TABLE mentorship_requests (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    mentor_id       BIGINT          NOT NULL,
    mentee_id       BIGINT          NOT NULL,
    message         TEXT,
    status          VARCHAR(15)     NOT NULL DEFAULT 'PENDING',
    requested_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    responded_at    TIMESTAMPTZ,
    CONSTRAINT pk_mentorship_requests PRIMARY KEY (id),
    CONSTRAINT fk_mr_mentor  FOREIGN KEY (mentor_id)  REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_mentee  FOREIGN KEY (mentee_id)  REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_mr_status CHECK (status IN ('PENDING','ACCEPTED','REJECTED','COMPLETED','CANCELLED'))
);
CREATE INDEX idx_mr_mentor ON mentorship_requests (mentor_id);
CREATE INDEX idx_mr_mentee ON mentorship_requests (mentee_id);
CREATE INDEX idx_mr_status ON mentorship_requests (status);

-- Platform Feedback
CREATE TABLE feedback_platform (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT          NOT NULL,
    topic       VARCHAR(100),
    rating      INT             NOT NULL,
    subject     VARCHAR(255),
    details     TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_feedback_platform PRIMARY KEY (id),
    CONSTRAINT fk_fp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Faculty Feedback (anonymous — no student FK stored)
CREATE TABLE feedback_faculty (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    faculty_id      BIGINT          NOT NULL,
    course_code     VARCHAR(20),
    semester        INT,
    clarity_score   INT             NOT NULL DEFAULT 0,
    pace_score      INT             NOT NULL DEFAULT 0,
    support_score   INT             NOT NULL DEFAULT 0,
    fairness_score  INT             NOT NULL DEFAULT 0,
    comments        TEXT,
    submitted_at    TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_feedback_faculty PRIMARY KEY (id),
    CONSTRAINT fk_ff_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_ff_faculty ON feedback_faculty (faculty_id);

-- Student Timetable
CREATE TABLE timetable_entries (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT          NOT NULL,
    course_code VARCHAR(20)     NOT NULL,
    course_name VARCHAR(255)    NOT NULL,
    class_time  VARCHAR(100),
    room        VARCHAR(50),
    faculty_id  BIGINT,
    day_of_week SMALLINT        NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_timetable_entries PRIMARY KEY (id),
    CONSTRAINT fk_tt_user    FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tt_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_tt_user_day ON timetable_entries (user_id, day_of_week);

-- Student Deadlines
CREATE TABLE deadlines (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT          NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    course_code VARCHAR(20),
    due_at      TIMESTAMPTZ     NOT NULL,
    urgency     VARCHAR(10)     NOT NULL DEFAULT 'MEDIUM',
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_deadlines PRIMARY KEY (id),
    CONSTRAINT fk_dl_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_dl_urgency CHECK (urgency IN ('HIGH','MEDIUM','LOW'))
);
CREATE INDEX idx_dl_user_due ON deadlines (user_id, due_at);
