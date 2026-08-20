package com.major.CampuSphere.service;

import com.major.CampuSphere.dto.request.AiChatRequest;
import com.major.CampuSphere.dto.request.AskDoubtRequest;
import com.major.CampuSphere.dto.response.AiResponse;
import com.major.CampuSphere.entity.AiConversation;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.Role;
import com.major.CampuSphere.repository.AiConversationRepository;
import com.major.CampuSphere.repository.LearningMaterialRepository;
import com.major.CampuSphere.repository.UserRepository;
import com.major.CampuSphere.service.impl.DemoAiServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DemoAiServiceTest {

    @Mock AiConversationRepository conversationRepo;
    @Mock LearningMaterialRepository materialRepo;
    @Mock UserRepository userRepo;

    @InjectMocks DemoAiServiceImpl aiService;

    private User testUser() {
        return User.builder().id(2L).name("Test Student")
                .email("s@test.edu").role(Role.STUDENT).active(true).build();
    }

    private AiConversation savedConversation(User user) {
        AiConversation conv = AiConversation.builder()
                .conversationKey(UUID.randomUUID().toString())
                .user(user)
                .conversationType("CHAT")
                .title("Test")
                .build();
        // set id via reflection would be needed for real test, mock save is enough
        return conv;
    }

    @Test
    void askDoubt_returnsNonNullAnswer() {
        User user = testUser();
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AskDoubtRequest req = new AskDoubtRequest();
        req.setQuestion("What is machine learning?");
        req.setDocumentTitle("Machine Learning Notes");

        AiResponse response = aiService.askDoubt(req, 2L);

        assertThat(response).isNotNull();
        assertThat(response.getAnswer()).isNotBlank();
        assertThat(response.isDemo()).isTrue();
        assertThat(response.getConversationId()).isNotNull();
    }

    @Test
    void chat_returnsNonNullReply() {
        User user = testUser();
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AiChatRequest req = new AiChatRequest();
        req.setMessage("Where is the library?");

        AiResponse response = aiService.chat(req, 2L);

        assertThat(response).isNotNull();
        assertThat(response.getReply()).isNotBlank();
        assertThat(response.isDemo()).isTrue();
    }

    @Test
    void chat_greetingMessage_returnsWelcomeResponse() {
        User user = testUser();
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AiChatRequest req = new AiChatRequest();
        req.setMessage("Hello");

        AiResponse response = aiService.chat(req, 2L);

        assertThat(response.getReply()).contains("Hello");
    }

    @Test
    void askDoubt_withExistingConversation_reusesConversation() {
        User user = testUser();
        String existingKey = UUID.randomUUID().toString();
        AiConversation existing = AiConversation.builder()
                .conversationKey(existingKey)
                .user(user)
                .conversationType("DOUBT")
                .title("ML Notes")
                .build();

        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(conversationRepo.findByConversationKeyAndUserId(existingKey, 2L))
                .thenReturn(Optional.of(existing));
        when(conversationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AskDoubtRequest req = new AskDoubtRequest();
        req.setConversationId(existingKey);
        req.setQuestion("What is supervised learning?");

        AiResponse response = aiService.askDoubt(req, 2L);

        assertThat(response.getConversationId()).isEqualTo(existingKey);
    }
}
