package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class AuthResponse {
    private String token;
    private String role;
    private String name;
    private String email;
    private Long userId;
}
