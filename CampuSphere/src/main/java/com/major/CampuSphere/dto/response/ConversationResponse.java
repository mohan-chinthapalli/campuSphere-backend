package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter @Builder
public class ConversationResponse {
    private String conversationId;
    private String title;
    private String conversationType;
    private List<MessageResponse> messages;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter @Builder
    public static class MessageResponse {
        private Long id;
        private String role;
        private String content;
        private boolean demo;
        private Instant createdAt;
    }
}
