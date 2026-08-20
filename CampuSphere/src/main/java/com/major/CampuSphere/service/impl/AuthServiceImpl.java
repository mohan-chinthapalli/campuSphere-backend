package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.LoginRequest;
import com.major.CampuSphere.dto.request.RegisterRequest;
import com.major.CampuSphere.dto.response.AuthResponse;
import com.major.CampuSphere.dto.response.UserResponse;
import com.major.CampuSphere.entity.FacultyProfile;
import com.major.CampuSphere.entity.StudentProfile;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.Role;
import com.major.CampuSphere.exception.DuplicateResourceException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.FacultyProfileRepository;
import com.major.CampuSphere.repository.StudentProfileRepository;
import com.major.CampuSphere.repository.UserRepository;
import com.major.CampuSphere.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final UserRepository userRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final FacultyProfileRepository facultyProfileRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();
        user = userRepo.save(user);

        // Create corresponding profile
        if (request.getRole() == Role.STUDENT) {
            StudentProfile profile = StudentProfile.builder()
                    .user(user)
                    .rollNumber(generateRollNumber(user.getId()))
                    .branch("Computer Science and Engineering")
                    .year("First Year · Semester 1")
                    .semester(1)
                    .build();
            studentProfileRepo.save(profile);
        } else if (request.getRole() == Role.FACULTY) {
            FacultyProfile profile = FacultyProfile.builder()
                    .user(user)
                    .build();
            facultyProfileRepo.save(profile);
        }

        log.info("New user registered: email={} role={}", user.getEmail(), user.getRole());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(),
                user.getRole().name(), user.getName());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Spring Security validates credentials; throws BadCredentialsException on failure
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("User logged in: email={} role={}", user.getEmail(), user.getRole());

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(),
                user.getRole().name(), user.getName());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .name(user.getName())
                .email(user.getEmail())
                .userId(user.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return toUserResponse(user);
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private String generateRollNumber(Long userId) {
        int year = java.time.Year.now().getValue() % 100;
        return String.format("CS%02dB%04d", year, userId);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
