-- ============================================================
-- V1: Initial Schema — Users, Profiles, Core Tables
-- ============================================================

-- Users (authentication)
CREATE TABLE users (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255)    NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    role        ENUM('STUDENT','FACULTY','ADMIN') NOT NULL DEFAULT 'STUDENT',
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Student Profiles
CREATE TABLE student_profiles (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    roll_number VARCHAR(20)     NOT NULL,
    branch      VARCHAR(100)    NOT NULL,
    year        VARCHAR(50)     NOT NULL,
    semester    INT             NOT NULL DEFAULT 1,
    cgpa        DECIMAL(4,2)    NOT NULL DEFAULT 0.00,
    attendance  DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    credits     INT             NOT NULL DEFAULT 0,
    bio         TEXT,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_sp_user_id (user_id),
    UNIQUE KEY uk_sp_roll_number (roll_number),
    CONSTRAINT fk_sp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Student Skills
CREATE TABLE student_skills (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    skill       VARCHAR(100)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sskill_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Faculty Profiles
CREATE TABLE faculty_profiles (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id         BIGINT UNSIGNED NOT NULL,
    title           VARCHAR(100),
    department      VARCHAR(100),
    office          VARCHAR(100),
    office_hours    VARCHAR(255),
    bio             TEXT,
    publications    INT             NOT NULL DEFAULT 0,
    citations       INT             NOT NULL DEFAULT 0,
    student_count   INT             NOT NULL DEFAULT 0,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_fp_user_id (user_id),
    CONSTRAINT fk_fp_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Campus Places (Navigation)
CREATE TABLE campus_places (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    slug        VARCHAR(50)     NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    type        VARCHAR(50)     NOT NULL,
    floors      INT             NOT NULL DEFAULT 1,
    open_hours  VARCHAR(100),
    map_x       DECIMAL(6,2)    NOT NULL DEFAULT 0,
    map_y       DECIMAL(6,2)    NOT NULL DEFAULT 0,
    walk_time   VARCHAR(50),
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_cp_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
