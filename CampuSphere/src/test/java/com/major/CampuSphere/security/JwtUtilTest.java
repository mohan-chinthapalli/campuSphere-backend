package com.major.CampuSphere.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-for-unit-tests-only-at-least-32-characters-long");
        props.setExpirationMs(3_600_000L);
        props.setRefreshExpirationMs(86_400_000L);
        jwtUtil = new JwtUtil(props);
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtUtil.generateToken(1L, "test@campusphere.edu", "STUDENT", "Test User");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
        assertThat(jwtUtil.extractUserId(token)).isEqualTo(1L);
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("test@campusphere.edu");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("STUDENT");
    }

    @Test
    void invalidToken_returnsFalse() {
        assertThat(jwtUtil.isTokenValid("invalid.token.here")).isFalse();
    }

    @Test
    void tamperedToken_returnsFalse() {
        String token = jwtUtil.generateToken(1L, "test@campusphere.edu", "STUDENT", "Test User");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtUtil.isTokenValid(tampered)).isFalse();
    }

    @Test
    void differentRoles_areStoredCorrectly() {
        String studentToken = jwtUtil.generateToken(1L, "s@test.edu", "STUDENT", "Student");
        String facultyToken = jwtUtil.generateToken(2L, "f@test.edu", "FACULTY", "Faculty");
        String adminToken   = jwtUtil.generateToken(3L, "a@test.edu", "ADMIN",   "Admin");

        assertThat(jwtUtil.extractRole(studentToken)).isEqualTo("STUDENT");
        assertThat(jwtUtil.extractRole(facultyToken)).isEqualTo("FACULTY");
        assertThat(jwtUtil.extractRole(adminToken)).isEqualTo("ADMIN");
    }
}
