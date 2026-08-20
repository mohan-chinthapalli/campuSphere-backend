package com.major.CampuSphere.service;

import com.major.CampuSphere.dto.request.LoginRequest;
import com.major.CampuSphere.dto.request.RegisterRequest;
import com.major.CampuSphere.dto.response.AuthResponse;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.Role;
import com.major.CampuSphere.exception.DuplicateResourceException;
import com.major.CampuSphere.repository.FacultyProfileRepository;
import com.major.CampuSphere.repository.StudentProfileRepository;
import com.major.CampuSphere.repository.UserRepository;
import com.major.CampuSphere.security.JwtUtil;
import com.major.CampuSphere.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepo;
    @Mock StudentProfileRepository studentProfileRepo;
    @Mock FacultyProfileRepository facultyProfileRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtil jwtUtil;

    @InjectMocks AuthServiceImpl authService;

    @Test
    void register_newUser_returnsToken() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test Student");
        req.setEmail("new@campusphere.edu");
        req.setPassword("Test@1234");
        req.setRole(Role.STUDENT);

        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");

        User saved = User.builder()
                .id(99L).name(req.getName()).email(req.getEmail())
                .password("hashed").role(Role.STUDENT).active(true).build();
        when(userRepo.save(any())).thenReturn(saved);
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(req);

        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getRole()).isEqualTo("STUDENT");
        assertThat(response.getEmail()).isEqualTo("new@campusphere.edu");
        verify(studentProfileRepo).save(any());
    }

    @Test
    void register_duplicateEmail_throwsDuplicateResourceException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("existing@campusphere.edu");
        req.setRole(Role.STUDENT);

        when(userRepo.existsByEmail("existing@campusphere.edu")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void login_validCredentials_returnsToken() {
        LoginRequest req = new LoginRequest();
        req.setEmail("student@campusphere.edu");
        req.setPassword("Demo@1234");

        User user = User.builder()
                .id(2L).name("Aarav Sharma").email(req.getEmail())
                .password("hashed").role(Role.STUDENT).active(true).build();

        when(userRepo.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(anyLong(), anyString(), anyString(), anyString()))
                .thenReturn("valid.jwt.token");

        AuthResponse response = authService.login(req);

        assertThat(response.getToken()).isEqualTo("valid.jwt.token");
        assertThat(response.getRole()).isEqualTo("STUDENT");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_invalidCredentials_throwsBadCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmail("bad@campusphere.edu");
        req.setPassword("wrongpass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
