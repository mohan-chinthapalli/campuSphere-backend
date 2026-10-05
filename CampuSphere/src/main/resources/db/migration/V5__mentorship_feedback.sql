-- ============================================================
-- V5: Mentorship, Feedback, Timetable, Deadlines
-- ============================================================

-- Mentor Profiles (extensions of student users who are mentors)
CREATE TABLE mentor_profiles (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id         BIGINT UNSIGNED NOT NULL,
    headline        VARCHAR(500),
    available       BOOLEAN         NOT NULL DEFAULT TRUE,
    total_sessions  INT             NOT NULL DEFAULT 0,
    avg_rating      DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    response_time   VARCHAR(20)     NOT NULL DEFAULT '~24h',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_mp_user (user_id),
    CONSTRAINT fk_mprof_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Mentorship Requests
CREATE TABLE mentorship_requests (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    mentor_id       BIGINT UNSIGNED NOT NULL,
    mentee_id       BIGINT UNSIGNED NOT NULL,
    message         TEXT,
    status          ENUM('PENDING','ACCEPTED','REJECTED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    requested_at    DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    responded_at    DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_mr_mentor  FOREIGN KEY (mentor_id)  REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_mentee  FOREIGN KEY (mentee_id)  REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_mr_mentor (mentor_id),
    INDEX idx_mr_mentee (mentee_id),
    INDEX idx_mr_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Platform Feedback
CREATE TABLE feedback_platform (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    topic       VARCHAR(100),
    rating      INT             NOT NULL,
    subject     VARCHAR(255),
    details     TEXT,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_fp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Faculty Feedback (anonymous — no student FK stored, responses are aggregate only)
-- NOTE: No student FK by design — faculty feedback is anonymous per product requirements
CREATE TABLE feedback_faculty (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    faculty_id      BIGINT UNSIGNED NOT NULL,
    course_code     VARCHAR(20),
    semester        INT,
    clarity_score   INT             NOT NULL DEFAULT 0,
    pace_score      INT             NOT NULL DEFAULT 0,
    support_score   INT             NOT NULL DEFAULT 0,
    fairness_score  INT             NOT NULL DEFAULT 0,
    comments        TEXT,
    submitted_at    DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_ff_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ff_faculty (faculty_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Student Timetable (classes per day)
CREATE TABLE timetable_entries (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    course_code VARCHAR(20)     NOT NULL,
    course_name VARCHAR(255)    NOT NULL,
    class_time  VARCHAR(100),
    room        VARCHAR(50),
    faculty_id  BIGINT UNSIGNED,
    day_of_week TINYINT         NOT NULL COMMENT '1=Monday, 7=Sunday',
    sort_order  INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_tt_user    FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tt_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_tt_user_day (user_id, day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Student Deadlines
CREATE TABLE deadlines (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    course_code VARCHAR(20),
    due_at      DATETIME(6)     NOT NULL,
    urgency     ENUM('HIGH','MEDIUM','LOW') NOT NULL DEFAULT 'MEDIUM',
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_dl_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_dl_user_due (user_id, due_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
