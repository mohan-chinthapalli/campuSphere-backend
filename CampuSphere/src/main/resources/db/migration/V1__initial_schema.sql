-- ============================================================
-- V1: Initial Schema — Users, Profiles, Core Tables
-- PostgreSQL-compatible (Supabase)
-- ============================================================

-- Users (authentication)
CREATE TABLE users (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    email       VARCHAR(255)    NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    role        VARCHAR(10)     NOT NULL DEFAULT 'STUDENT',
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('STUDENT','FACULTY','ADMIN'))
);

-- Student Profiles
CREATE TABLE student_profiles (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT          NOT NULL,
    roll_number VARCHAR(20)     NOT NULL,
    branch      VARCHAR(100)    NOT NULL,
    academic_year VARCHAR(50)   NOT NULL,
    semester    INT             NOT NULL DEFAULT 1,
    cgpa        DECIMAL(4,2)    NOT NULL DEFAULT 0.00,
    attendance  DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    credits     INT             NOT NULL DEFAULT 0,
    bio         TEXT,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_student_profiles PRIMARY KEY (id),
    CONSTRAINT uk_sp_user_id UNIQUE (user_id),
    CONSTRAINT uk_sp_roll_number UNIQUE (roll_number),
    CONSTRAINT fk_sp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Student Skills
CREATE TABLE student_skills (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT          NOT NULL,
    skill       VARCHAR(100)    NOT NULL,
    CONSTRAINT pk_student_skills PRIMARY KEY (id),
    CONSTRAINT fk_sskill_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Faculty Profiles
CREATE TABLE faculty_profiles (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id         BIGINT          NOT NULL,
    title           VARCHAR(100),
    department      VARCHAR(100),
    office          VARCHAR(100),
    office_hours    VARCHAR(255),
    bio             TEXT,
    publications    INT             NOT NULL DEFAULT 0,
    citations       INT             NOT NULL DEFAULT 0,
    student_count   INT             NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_faculty_profiles PRIMARY KEY (id),
    CONSTRAINT uk_fp_user_id UNIQUE (user_id),
    CONSTRAINT fk_fp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Campus Places (Navigation)
CREATE TABLE campus_places (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    slug        VARCHAR(50)     NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    type        VARCHAR(50)     NOT NULL,
    floors      INT             NOT NULL DEFAULT 1,
    open_hours  VARCHAR(100),
    map_x       DECIMAL(6,2)    NOT NULL DEFAULT 0,
    map_y       DECIMAL(6,2)    NOT NULL DEFAULT 0,
    walk_time   VARCHAR(50),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_campus_places PRIMARY KEY (id),
    CONSTRAINT uk_cp_slug UNIQUE (slug)
);
