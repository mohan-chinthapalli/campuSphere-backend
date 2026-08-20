package com.major.CampuSphere.service;

import com.major.CampuSphere.dto.request.AiChatRequest;
import com.major.CampuSphere.dto.request.AskDoubtRequest;
import com.major.CampuSphere.dto.response.AiResponse;
import com.major.CampuSphere.dto.response.ConversationResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * AI service interface.
 *
 * Current implementation: DemoAiServiceImpl — returns canned responses.
 * Future implementation: RealAiServiceImpl (RAG + LLM) can be swapped in
 * by changing the @Primary annotation without touching the controller or API contract.
 */
public interface AiService {

    /**
     * Ask a study doubt in the context of a learning material.
     * Used by the AI Doubt Panel in the material reader.
     */
    AiResponse askDoubt(AskDoubtRequest request, Long userId);

    /**
     * Send a general campus AI chat message.
     * Used by the general /app/ai chatbot page.
     */
    AiResponse chat(AiChatRequest request, Long userId);

    /**
     * List all conversations for a user.
     */
    PageResponse<ConversationResponse> listConversations(Long userId, Pageable pageable);

    /**
     * Get full conversation history by conversationKey.
     * Only the owning user may access.
     */
    ConversationResponse getConversation(String conversationId, Long userId);
}
