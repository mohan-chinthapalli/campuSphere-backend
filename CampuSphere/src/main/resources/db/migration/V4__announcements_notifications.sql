-- ============================================================
-- V4: Announcements & Notifications
-- PostgreSQL-compatible (Supabase)
-- ============================================================

-- Announcements
CREATE TABLE announcements (
    id          BIGINT          GENERATED ALWAYS AS IDENTITY,
    title       VARCHAR(500)    NOT NULL,
    body        TEXT            NOT NULL,
    author      VARCHAR(100)    NOT NULL,
    tag         VARCHAR(20)     NOT NULL DEFAULT 'GENERAL',
    priority    VARCHAR(10)     NOT NULL DEFAULT 'NORMAL',
    created_by  BIGINT,
    published_at TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_announcements PRIMARY KEY (id),
    CONSTRAINT fk_ann_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_ann_tag CHECK (tag IN ('ACADEMICS','CAMPUS','PLACEMENTS','RESEARCH','EVENTS','GENERAL')),
    CONSTRAINT chk_ann_priority CHECK (priority IN ('HIGH','NORMAL'))
);
CREATE INDEX idx_ann_tag ON announcements (tag);
CREATE INDEX idx_ann_priority ON announcements (priority);
CREATE INDEX idx_ann_published ON announcements (published_at);

-- Notifications
CREATE TABLE notifications (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY,
    user_id             BIGINT          NOT NULL,
    title               VARCHAR(255)    NOT NULL,
    body                TEXT,
    notification_type   VARCHAR(20)     NOT NULL DEFAULT 'SYSTEM',
    reference_id        BIGINT,
    is_read             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_notif_type CHECK (notification_type IN ('EVENT','CLUB','ANNOUNCEMENT','ACADEMIC','MENTORSHIP','SYSTEM'))
);
CREATE INDEX idx_notif_user ON notifications (user_id);
CREATE INDEX idx_notif_read ON notifications (user_id, is_read);
