-- ============================================================
-- V2: Events and Clubs
-- ============================================================

-- Events
CREATE TABLE events (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    slug            VARCHAR(100)    NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    category        VARCHAR(50)     NOT NULL,
    tagline         VARCHAR(500),
    description     TEXT,
    prize           VARCHAR(255),
    event_date      VARCHAR(100),
    starts_at       DATETIME(6),
    display_time    VARCHAR(100),
    venue           VARCHAR(255),
    seats_total     INT             NOT NULL DEFAULT 0,
    organizer_name  VARCHAR(100),
    organizer_club  VARCHAR(100),
    organizer_email VARCHAR(255),
    gradient        VARCHAR(255),
    emoji           VARCHAR(10),
    tags            TEXT,
    created_by      BIGINT UNSIGNED,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_events_slug (slug),
    CONSTRAINT fk_event_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_events_category (category),
    INDEX idx_events_starts_at (starts_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event Agenda
CREATE TABLE event_agenda (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    event_id    BIGINT UNSIGNED NOT NULL,
    agenda_time VARCHAR(50)     NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_agenda_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event Registrations
CREATE TABLE event_registrations (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    event_id        BIGINT UNSIGNED NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    registered_at   DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_event_reg (event_id, user_id),
    CONSTRAINT fk_ereg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_ereg_user  FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Event Discussion
CREATE TABLE event_discussions (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    event_id    BIGINT UNSIGNED NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    message     TEXT            NOT NULL,
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_ediscuss_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_ediscuss_user  FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ediscuss_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Clubs
CREATE TABLE clubs (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    slug            VARCHAR(100)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    category        VARCHAR(50)     NOT NULL,
    tagline         VARCHAR(500),
    about           TEXT,
    mission         TEXT,
    member_count    INT             NOT NULL DEFAULT 0,
    recruiting      BOOLEAN         NOT NULL DEFAULT FALSE,
    coordinator_id  BIGINT UNSIGNED,
    gradient        VARCHAR(255),
    emoji           VARCHAR(10),
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_clubs_slug (slug),
    CONSTRAINT fk_club_coord FOREIGN KEY (coordinator_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_clubs_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Club Achievements
CREATE TABLE club_achievements (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    club_id     BIGINT UNSIGNED NOT NULL,
    achievement VARCHAR(500)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_cach_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Club Leads
CREATE TABLE club_leads (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    club_id     BIGINT UNSIGNED NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    role        VARCHAR(100)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_clead_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Club Memberships
CREATE TABLE club_memberships (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    club_id     BIGINT UNSIGNED NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    club_role   ENUM('MEMBER','LEAD','COORDINATOR') NOT NULL DEFAULT 'MEMBER',
    joined_at   DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_club_member (club_id, user_id),
    CONSTRAINT fk_cmem_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmem_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
