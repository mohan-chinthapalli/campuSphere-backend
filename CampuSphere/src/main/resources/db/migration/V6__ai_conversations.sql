-- ============================================================
-- V6: AI Conversations & Messages
-- ============================================================

-- AI Conversations
CREATE TABLE ai_conversations (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    conversation_key VARCHAR(36)    NOT NULL COMMENT 'UUID used as public reference',
    user_id         BIGINT UNSIGNED NOT NULL,
    title           VARCHAR(500),
    document_id     BIGINT UNSIGNED COMMENT 'Optional: material context',
    conversation_type ENUM('DOUBT','CHAT') NOT NULL DEFAULT 'CHAT',
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_conv_key (conversation_key),
    CONSTRAINT fk_conv_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conv_doc  FOREIGN KEY (document_id) REFERENCES learning_materials(id) ON DELETE SET NULL,
    INDEX idx_conv_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- AI Messages
CREATE TABLE ai_messages (
    id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    conversation_id BIGINT UNSIGNED NOT NULL,
    role            ENUM('USER','ASSISTANT') NOT NULL,
    content         TEXT            NOT NULL,
    is_demo         BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT fk_msg_conv FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id) ON DELETE CASCADE,
    INDEX idx_msg_conv (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
