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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock AiConversationRepository conversationRepo;
    @Mock LearningMaterialRepository materialRepo;
    @Mock UserRepository userRepo;

    @InjectMocks DemoAiServiceImpl aiService;

    private User mockUser() {
        return User.builder().id(1L).email("student@campusphere.edu")
                .name("Aarav Sharma").role(Role.STUDENT).build();
    }

    private AiConversation mockConversation(User user) {
        return AiConversation.builder()
                .conversationKey("test-uuid-1234")
                .user(user)
                .conversationType("CHAT")
                .title("Test Conversation")
                .build();
    }

    @Test
    void askDoubt_returnsNonNullAnswer() {
        User user = mockUser();
        AiConversation conv = mockConversation(user);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(conversationRepo.save(any())).thenReturn(conv);

        AskDoubtRequest request = new AskDoubtRequest();
        request.setQuestion("What is machine learning?");
        request.setDocumentTitle("Machine Learning Notes");

        AiResponse response = aiService.askDoubt(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getAnswer()).isNotBlank();
        assertThat(response.isDemo()).isTrue();
        assertThat(response.getConversationId()).isNotNull();
    }

    @Test
    void chat_hello_returnsGreeting() {
        User user = mockUser();
        AiConversation conv = mockConversation(user);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(conversationRepo.save(any())).thenReturn(conv);

        AiChatRequest request = new AiChatRequest();
        request.setMessage("Hello");

        AiResponse response = aiService.chat(request, 1L);

        assertThat(response).isNotNull();
        assertThat(response.getReply()).containsIgnoringCase("campus");
        assertThat(response.isDemo()).isTrue();
    }

    @Test
    void chat_eventQuery_mentionsEvents() {
        User user = mockUser();
        AiConversation conv = mockConversation(user);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(conversationRepo.save(any())).thenReturn(conv);

        AiChatRequest request = new AiChatRequest();
        request.setMessage("What events are coming up?");

        AiResponse response = aiService.chat(request, 1L);

        assertThat(response.getReply()).containsIgnoringCase("event");
    }
}
