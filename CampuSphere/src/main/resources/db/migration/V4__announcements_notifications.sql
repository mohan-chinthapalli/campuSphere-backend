-- ============================================================
-- V4: Announcements & Notifications
-- ============================================================

-- Announcements
CREATE TABLE announcements (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    title       VARCHAR(500)    NOT NULL,
    body        TEXT            NOT NULL,
    author      VARCHAR(100)    NOT NULL,
    tag         ENUM('ACADEMICS','CAMPUS','PLACEMENTS','RESEARCH','EVENTS','GENERAL') NOT NULL DEFAULT 'GENERAL',
    priority    ENUM('HIGH','NORMAL') NOT NULL DEFAULT 'NORMAL',
    created_by  BIGINT UNSIGNED,
    published_at DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_ann_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_ann_tag (tag),
    INDEX idx_ann_priority (priority),
    INDEX idx_ann_published (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notifications
CREATE TABLE notifications (
    id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id             BIGINT UNSIGNED NOT NULL,
    title               VARCHAR(255)    NOT NULL,
    body                TEXT,
    notification_type   ENUM('EVENT','CLUB','ANNOUNCEMENT','ACADEMIC','MENTORSHIP','SYSTEM') NOT NULL DEFAULT 'SYSTEM',
    reference_id        BIGINT UNSIGNED COMMENT 'FK to related entity (event/club/etc.)',
    is_read             BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at          DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user (user_id),
    INDEX idx_notif_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
