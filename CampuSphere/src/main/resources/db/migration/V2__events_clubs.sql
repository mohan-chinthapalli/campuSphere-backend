-- ============================================================
-- V2: Events and Clubs
-- PostgreSQL-compatible (Supabase)
-- ============================================================

-- Events
CREATE TABLE events (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    slug            VARCHAR(100)    NOT NULL,
    title           VARCHAR(255)    NOT NULL,
    category        VARCHAR(50)     NOT NULL,
    tagline         VARCHAR(500),
    description     TEXT,
    prize           VARCHAR(255),
    event_date      VARCHAR(100),
    starts_at       TIMESTAMPTZ,
    display_time    VARCHAR(100),
    venue           VARCHAR(255),
    seats_total     INT             NOT NULL DEFAULT 0,
    organizer_name  VARCHAR(100),
    organizer_club  VARCHAR(100),
    organizer_email VARCHAR(255),
    gradient        VARCHAR(255),
    emoji           VARCHAR(10),
    tags            TEXT,
    created_by      BIGINT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_events PRIMARY KEY (id),
    CONSTRAINT uk_events_slug UNIQUE (slug),
    CONSTRAINT fk_event_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_events_category ON events (category);
CREATE INDEX idx_events_starts_at ON events (starts_at);

-- Event Agenda
CREATE TABLE event_agenda (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    event_id    BIGINT          NOT NULL,
    agenda_time VARCHAR(50)     NOT NULL,
    title       VARCHAR(255)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_event_agenda PRIMARY KEY (id),
    CONSTRAINT fk_agenda_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- Event Registrations
CREATE TABLE event_registrations (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    event_id        BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    registered_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_event_registrations PRIMARY KEY (id),
    CONSTRAINT uk_event_reg UNIQUE (event_id, user_id),
    CONSTRAINT fk_ereg_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_ereg_user  FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE CASCADE
);

-- Event Discussions
CREATE TABLE event_discussions (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    event_id    BIGINT          NOT NULL,
    user_id     BIGINT          NOT NULL,
    message     TEXT            NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_event_discussions PRIMARY KEY (id),
    CONSTRAINT fk_ediscuss_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    CONSTRAINT fk_ediscuss_user  FOREIGN KEY (user_id)  REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_ediscuss_event ON event_discussions (event_id);

-- Clubs
CREATE TABLE clubs (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    slug            VARCHAR(100)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    category        VARCHAR(50)     NOT NULL,
    tagline         VARCHAR(500),
    about           TEXT,
    mission         TEXT,
    member_count    INT             NOT NULL DEFAULT 0,
    recruiting      BOOLEAN         NOT NULL DEFAULT FALSE,
    coordinator_id  BIGINT,
    gradient        VARCHAR(255),
    emoji           VARCHAR(10),
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_clubs PRIMARY KEY (id),
    CONSTRAINT uk_clubs_slug UNIQUE (slug),
    CONSTRAINT fk_club_coord FOREIGN KEY (coordinator_id) REFERENCES users(id) ON DELETE SET NULL
);
CREATE INDEX idx_clubs_category ON clubs (category);

-- Club Achievements
CREATE TABLE club_achievements (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    club_id     BIGINT          NOT NULL,
    achievement VARCHAR(500)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_club_achievements PRIMARY KEY (id),
    CONSTRAINT fk_cach_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
);

-- Club Leads
CREATE TABLE club_leads (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    club_id     BIGINT          NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    role        VARCHAR(100)    NOT NULL,
    CONSTRAINT pk_club_leads PRIMARY KEY (id),
    CONSTRAINT fk_clead_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE
);

-- Club Memberships
CREATE TABLE club_memberships (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    club_id     BIGINT          NOT NULL,
    user_id     BIGINT          NOT NULL,
    club_role   VARCHAR(15)     NOT NULL DEFAULT 'MEMBER',
    joined_at   TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_club_memberships PRIMARY KEY (id),
    CONSTRAINT uk_club_member UNIQUE (club_id, user_id),
    CONSTRAINT fk_cmem_club FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    CONSTRAINT fk_cmem_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_club_role CHECK (club_role IN ('MEMBER','LEAD','COORDINATOR'))
);
