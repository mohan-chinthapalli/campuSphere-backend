-- ============================================================
-- V6: AI Conversations & Messages
-- PostgreSQL-compatible (Supabase)
-- ============================================================

-- AI Conversations
CREATE TABLE ai_conversations (
    id                  BIGINT          GENERATED ALWAYS AS IDENTITY,
    conversation_key    VARCHAR(36)     NOT NULL,
    user_id             BIGINT          NOT NULL,
    title               VARCHAR(500),
    document_id         BIGINT,
    conversation_type   VARCHAR(10)     NOT NULL DEFAULT 'CHAT',
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_ai_conversations PRIMARY KEY (id),
    CONSTRAINT uk_conv_key UNIQUE (conversation_key),
    CONSTRAINT fk_conv_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_conv_doc  FOREIGN KEY (document_id) REFERENCES learning_materials(id) ON DELETE SET NULL,
    CONSTRAINT chk_conv_type CHECK (conversation_type IN ('DOUBT','CHAT'))
);
CREATE INDEX idx_conv_user ON ai_conversations (user_id);

-- AI Messages
CREATE TABLE ai_messages (
    id              BIGINT          GENERATED ALWAYS AS IDENTITY,
    conversation_id BIGINT          NOT NULL,
    role            VARCHAR(10)     NOT NULL,
    content         TEXT            NOT NULL,
    is_demo         BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_ai_messages PRIMARY KEY (id),
    CONSTRAINT fk_msg_conv FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id) ON DELETE CASCADE,
    CONSTRAINT chk_msg_role CHECK (role IN ('USER','ASSISTANT'))
);
CREATE INDEX idx_msg_conv ON ai_messages (conversation_id);
