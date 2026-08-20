package com.major.CampuSphere.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.major.CampuSphere.dto.request.LoginRequest;
import com.major.CampuSphere.dto.request.RegisterRequest;
import com.major.CampuSphere.dto.response.AuthResponse;
import com.major.CampuSphere.enums.Role;
import com.major.CampuSphere.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer integration tests for authentication endpoints.
 * Uses full Spring context with H2 (test profile) so the security filter chain
 * is wired correctly. AuthServiceImpl is mocked to keep tests fast.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthServiceImpl authService;

    @Test
    void login_validRequest_returns200() throws Exception {
        AuthResponse mockResponse = AuthResponse.builder()
                .token("mock.token").role("STUDENT")
                .name("Test").email("s@test.edu").userId(1L).build();

        when(authService.login(any())).thenReturn(mockResponse);

        LoginRequest req = new LoginRequest();
        req.setEmail("s@test.edu");
        req.setPassword("Test@1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock.token"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    void login_missingEmail_returns400() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setPassword("Test@1234");
        // email intentionally missing

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest req = new LoginRequest();
        req.setEmail("bad@test.edu");
        req.setPassword("wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void register_validRequest_returns201() throws Exception {
        AuthResponse mockResponse = AuthResponse.builder()
                .token("new.token").role("STUDENT")
                .name("New User").email("new@test.edu").userId(10L).build();

        when(authService.register(any())).thenReturn(mockResponse);

        RegisterRequest req = new RegisterRequest();
        req.setName("New User");
        req.setEmail("new@test.edu");
        req.setPassword("Test@1234");
        req.setRole(Role.STUDENT);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("new.token"));
    }
}
