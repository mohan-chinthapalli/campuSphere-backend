package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class AskDoubtRequest {

    private String conversationId; // UUID — null means start new conversation

    @NotBlank(message = "Question is required")
    private String question;

    private String documentContext;   // full extracted text of open document
    private String documentTitle;
    private String currentSection;
    private String documentId;        // materialKey (e.g. "m-6-CS601-NOTES")
    private List<ChatMessageDto> history;

    @Getter @Setter
    public static class ChatMessageDto {
        private String role;    // "user" or "assistant"
        private String content;
    }
}
