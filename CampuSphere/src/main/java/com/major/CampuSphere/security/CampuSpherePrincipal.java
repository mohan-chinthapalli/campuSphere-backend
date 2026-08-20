package com.major.CampuSphere.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Authenticated principal stored in SecurityContext.
 * Carries userId so services can perform ownership checks without an extra DB lookup.
 */
@Getter
@AllArgsConstructor
public class CampuSpherePrincipal {
    private final Long userId;
    private final String email;
    private final String role;
}
