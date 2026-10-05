-- ============================================================
-- V3: Learning Hub — Subjects, Materials, Progress
-- PostgreSQL-compatible (Supabase)
-- ============================================================

-- Subjects
CREATE TABLE subjects (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    code        VARCHAR(20)     NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    emoji       VARCHAR(10),
    semester    INT             NOT NULL,
    faculty_id  BIGINT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_subjects PRIMARY KEY (id),
    CONSTRAINT uk_subject_code UNIQUE (code),
    CONSTRAINT fk_subj_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_subject_semester ON subjects (semester);

-- Learning Materials
CREATE TABLE learning_materials (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    material_key    VARCHAR(100)    NOT NULL,
    title           VARCHAR(500)    NOT NULL,
    material_type   VARCHAR(20)     NOT NULL,
    subject_code    VARCHAR(20)     NOT NULL,
    semester        INT             NOT NULL,
    author          VARCHAR(100),
    file_size       VARCHAR(20),
    download_count  INT             NOT NULL DEFAULT 0,
    file_path       VARCHAR(1000),
    emoji           VARCHAR(10),
    gradient        VARCHAR(255),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_learning_materials PRIMARY KEY (id),
    CONSTRAINT uk_material_key UNIQUE (material_key),
    CONSTRAINT fk_mat_subject FOREIGN KEY (subject_code) REFERENCES subjects(code) ON DELETE RESTRICT,
    CONSTRAINT chk_material_type CHECK (material_type IN ('NOTES','PDF','VIDEO','PAPER','HANDWRITTEN','LAB_MANUAL','SYLLABUS'))
);
CREATE INDEX idx_mat_semester ON learning_materials (semester);
CREATE INDEX idx_mat_type ON learning_materials (material_type);
CREATE INDEX idx_mat_subject ON learning_materials (subject_code);

-- Material Progress (per user)
CREATE TABLE material_progress (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    material_id     BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    progress_pct    INT             NOT NULL DEFAULT 0,
    bookmarked      BOOLEAN         NOT NULL DEFAULT FALSE,
    last_read_at    TIMESTAMPTZ,
    CONSTRAINT pk_material_progress PRIMARY KEY (id),
    CONSTRAINT uk_mat_progress UNIQUE (material_id, user_id),
    CONSTRAINT fk_mp_material FOREIGN KEY (material_id) REFERENCES learning_materials(id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE
);

-- Skill Sessions (Faculty Skill Hub)
CREATE TABLE skill_sessions (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    slug            VARCHAR(100)    NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    faculty_id      BIGINT,
    department      VARCHAR(100),
    category        VARCHAR(100)    NOT NULL,
    level           VARCHAR(15)     NOT NULL,
    total_sessions  INT             NOT NULL DEFAULT 1,
    duration        VARCHAR(50),
    enrolled_count  INT             NOT NULL DEFAULT 0,
    rating          DECIMAL(3,2)    NOT NULL DEFAULT 0.00,
    schedule        VARCHAR(255),
    emoji           VARCHAR(10),
    gradient        VARCHAR(255),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_skill_sessions PRIMARY KEY (id),
    CONSTRAINT uk_ss_slug UNIQUE (slug),
    CONSTRAINT fk_ss_faculty FOREIGN KEY (faculty_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_session_level CHECK (level IN ('BEGINNER','INTERMEDIATE','ADVANCED'))
);
CREATE INDEX idx_ss_category ON skill_sessions (category);

-- Skill Session Outcomes
CREATE TABLE skill_session_outcomes (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    session_id      BIGINT          NOT NULL,
    outcome         VARCHAR(500)    NOT NULL,
    sort_order      INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_skill_session_outcomes PRIMARY KEY (id),
    CONSTRAINT fk_sso_session FOREIGN KEY (session_id) REFERENCES skill_sessions(id) ON DELETE CASCADE
);

-- Session Enrollments
CREATE TABLE session_enrollments (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY,
    session_id          BIGINT          NOT NULL,
    user_id             BIGINT          NOT NULL,
    completed_sessions  INT             NOT NULL DEFAULT 0,
    last_activity_at    TIMESTAMPTZ,
    enrolled_at         TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_session_enrollments PRIMARY KEY (id),
    CONSTRAINT uk_senroll UNIQUE (session_id, user_id),
    CONSTRAINT fk_se_session FOREIGN KEY (session_id) REFERENCES skill_sessions(id) ON DELETE CASCADE,
    CONSTRAINT fk_se_user    FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE
);
