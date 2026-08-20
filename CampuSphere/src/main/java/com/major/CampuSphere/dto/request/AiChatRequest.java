package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AiChatRequest {

    private String conversationId; // null = new conversation

    @NotBlank(message = "Message is required")
    private String message;
}
