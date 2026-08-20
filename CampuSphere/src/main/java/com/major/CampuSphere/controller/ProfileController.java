package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.UpdateFacultyProfileRequest;
import com.major.CampuSphere.dto.request.UpdateStudentProfileRequest;
import com.major.CampuSphere.dto.response.*;
import com.major.CampuSphere.enums.Role;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.AuthServiceImpl;
import com.major.CampuSphere.service.impl.ProfileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Student and faculty profile management")
public class ProfileController {

    private final ProfileServiceImpl profileService;
    private final AuthServiceImpl authService;

    // ─── Current user profile (role-aware) ────────────────────────

    @GetMapping
    @Operation(summary = "Get profile of the currently authenticated user")
    public ResponseEntity<ApiResponse<?>> getMyProfile(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return switch (principal.getRole()) {
            case "STUDENT" -> ResponseEntity.ok(ApiResponse.success(
                    profileService.getStudentProfile(principal.getUserId())));
            case "FACULTY" -> ResponseEntity.ok(ApiResponse.success(
                    profileService.getFacultyProfile(principal.getUserId())));
            default -> ResponseEntity.ok(ApiResponse.success(
                    authService.getCurrentUser(principal.getUserId())));
        };
    }

    @PutMapping
    @Operation(summary = "Update profile of the currently authenticated user (role-aware)")
    public ResponseEntity<ApiResponse<?>> updateMyProfile(
            @AuthenticationPrincipal CampuSpherePrincipal principal,
            @Valid @RequestBody(required = false) Object body) {
        // Redirect clients to role-specific endpoints — documented in API contract
        if ("STUDENT".equals(principal.getRole())) {
            return ResponseEntity.ok(ApiResponse.error(
                    "Use PUT /api/profile/student to update a student profile",
                    "USE_SPECIFIC_ENDPOINT", "/api/profile/student"));
        }
        return ResponseEntity.ok(ApiResponse.error(
                "Use PUT /api/profile/faculty to update a faculty profile",
                "USE_SPECIFIC_ENDPOINT", "/api/profile/faculty"));
    }

    // ─── Student profile ──────────────────────────────────────────

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get current student profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getStudentProfile(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                profileService.getStudentProfile(principal.getUserId())));
    }

    @PutMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update current student profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateStudentProfile(
            @Valid @RequestBody UpdateStudentProfileRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                profileService.updateStudentProfile(principal.getUserId(), request)));
    }

    // ─── Faculty profile ──────────────────────────────────────────

    @GetMapping("/faculty")
    @PreAuthorize("hasRole('FACULTY')")
    @Operation(summary = "Get current faculty profile")
    public ResponseEntity<ApiResponse<FacultyProfileResponse>> getFacultyProfile(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success(
                profileService.getFacultyProfile(principal.getUserId())));
    }

    @PutMapping("/faculty")
    @PreAuthorize("hasRole('FACULTY')")
    @Operation(summary = "Update current faculty profile")
    public ResponseEntity<ApiResponse<FacultyProfileResponse>> updateFacultyProfile(
            @Valid @RequestBody UpdateFacultyProfileRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                profileService.updateFacultyProfile(principal.getUserId(), request)));
    }

    // ─── Public lookup by user ID ─────────────────────────────────

    @GetMapping("/faculty/{userId}")
    @Operation(summary = "Get faculty profile by user ID (public read)")
    public ResponseEntity<ApiResponse<FacultyProfileResponse>> getFacultyById(
            @PathVariable Long userId) {

        return ResponseEntity.ok(ApiResponse.success(
                profileService.getFacultyProfile(userId)));
    }
}
