-- ============================================================
-- V3: Learning Hub — Subjects, Materials, Progress
-- ============================================================

-- Subjects
CREATE TABLE subjects (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    code        VARCHAR(20)     NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    emoji       VARCHAR(10),
    semester    INT             NOT NULL,
    faculty_id  BIGINT UNSIGNED,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_subject_code (code),
    CONSTRAINT fk_subj_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_subject_semester (semester)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Learning Materials
CREATE TABLE learning_materials (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    material_key    VARCHAR(100)    NOT NULL COMMENT 'Frontend ID: m-{sem}-{code}-{type}',
    title           VARCHAR(500)    NOT NULL,
    material_type   ENUM('NOTES','PDF','VIDEO','PAPER','HANDWRITTEN','LAB_MANUAL','SYLLABUS') NOT NULL,
    subject_code    VARCHAR(20)     NOT NULL,
    semester        INT             NOT NULL,
    author          VARCHAR(100),
    file_size       VARCHAR(20),
    download_count  INT             NOT NULL DEFAULT 0,
    file_path       VARCHAR(1000)   COMMENT 'Relative path or storage key',
    emoji           VARCHAR(10),
    gradient        VARCHAR(255),
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_material_key (material_key),
    CONSTRAINT fk_mat_subject FOREIGN KEY (subject_code) REFERENCES subjects(code) ON DELETE RESTRICT,
    INDEX idx_mat_semester (semester),
    INDEX idx_mat_type (material_type),
    INDEX idx_mat_subject (subject_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Material Progress (per user)
CREATE TABLE material_progress (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    material_id     BIGINT UNSIGNED NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    progress_pct    INT             NOT NULL DEFAULT 0,
    bookmarked      BOOLEAN         NOT NULL DEFAULT FALSE,
    last_read_at    DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_mat_progress (material_id, user_id),
    CONSTRAINT fk_mp_material FOREIGN KEY (material_id) REFERENCES learning_materials(id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Skill Sessions (Faculty Skill Hub)
CREATE TABLE skill_sessions (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    slug            VARCHAR(100)    NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    faculty_id      BIGINT UNSIGNED,
    department      VARCHAR(100),
    category        VARCHAR(100)    NOT NULL,
    level           ENUM('BEGINNER','INTERMEDIATE','ADVANCED') NOT NULL,
    total_sessions  INT             NOT NULL DEFAULT 1,
    duration        VARCHAR(50),
    enrolled_count  INT             NOT NULL DEFAULT 0,
    rating          DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    schedule        VARCHAR(255),
    emoji           VARCHAR(10),
    gradient        VARCHAR(255),
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_ss_slug (slug),
    CONSTRAINT fk_ss_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_ss_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Skill Session Outcomes
CREATE TABLE skill_session_outcomes (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    session_id      BIGINT UNSIGNED NOT NULL,
    outcome         VARCHAR(500)    NOT NULL,
    sort_order      INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_sso_session FOREIGN KEY (session_id) REFERENCES skill_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Session Enrollments
CREATE TABLE session_enrollments (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    session_id          BIGINT UNSIGNED NOT NULL,
    user_id             BIGINT UNSIGNED NOT NULL,
    completed_sessions  INT             NOT NULL DEFAULT 0,
    last_activity_at    DATETIME(6),
    enrolled_at         DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_senroll (session_id, user_id),
    CONSTRAINT fk_se_session FOREIGN KEY (session_id) REFERENCES skill_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_se_user    FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
