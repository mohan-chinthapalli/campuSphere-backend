package com.major.CampuSphere.dto.response;

import com.major.CampuSphere.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter @Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private boolean active;
    private Instant createdAt;
}
