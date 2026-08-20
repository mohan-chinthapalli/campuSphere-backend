package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.AiChatRequest;
import com.major.CampuSphere.dto.request.AskDoubtRequest;
import com.major.CampuSphere.dto.response.*;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Assistant", description = "Campus AI chat and Learning Hub doubt panel")
public class AiController {

    private final AiService aiService;

    /**
     * AI Doubt Panel — used inside the Learning Hub material reader.
     * Matches the frontend's askStudyAssistant() call pattern.
     * POST /api/ai/ask-doubt
     */
    @PostMapping("/ask-doubt")
    @Operation(summary = "Ask a study doubt (Learning Hub AI Doubt Panel)")
    public ResponseEntity<ApiResponse<AiResponse>> askDoubt(
            @Valid @RequestBody AskDoubtRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        AiResponse response = aiService.askDoubt(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Response generated", response));
    }

    /**
     * General Campus AI Chat — used on the /app/ai page.
     * POST /api/ai/chat
     */
    @PostMapping("/chat")
    @Operation(summary = "Send a message to the general campus AI chatbot")
    public ResponseEntity<ApiResponse<AiResponse>> chat(
            @Valid @RequestBody AiChatRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        AiResponse response = aiService.chat(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Response generated", response));
    }

    /**
     * List all AI conversations for the current user.
     */
    @GetMapping("/conversations")
    @Operation(summary = "List AI conversation history for current user")
    public ResponseEntity<ApiResponse<PageResponse<ConversationResponse>>> listConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Conversations retrieved",
                aiService.listConversations(principal.getUserId(), PageRequest.of(page, size))));
    }

    /**
     * Get a specific conversation by its UUID key.
     */
    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Get full conversation history by ID")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversation(
            @PathVariable String conversationId,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                aiService.getConversation(conversationId, principal.getUserId())));
    }
}
