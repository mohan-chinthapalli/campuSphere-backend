package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.dto.response.SkillSessionResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.SkillSessionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@Tag(name = "Skill Sessions", description = "Faculty Skill Hub — browse and enroll in sessions")
public class SkillSessionController {

    private final SkillSessionServiceImpl skillSessionService;

    @GetMapping
    @Operation(summary = "List/search skill sessions")
    public ResponseEntity<ApiResponse<PageResponse<SkillSessionResponse>>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("rating").descending());
        return ResponseEntity.ok(ApiResponse.success("Sessions retrieved",
                skillSessionService.list(category, q, userId, pageable)));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get skill session by slug")
    public ResponseEntity<ApiResponse<SkillSessionResponse>> getBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.success(
                skillSessionService.getBySlug(slug, userId)));
    }

    @PostMapping("/{slug}/enroll")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Enroll in a skill session (auth required)")
    public ResponseEntity<ApiResponse<SkillSessionResponse>> enroll(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        // principal is guaranteed non-null here — security config requires auth for POST
        return ResponseEntity.ok(ApiResponse.success("Enrolled in session",
                skillSessionService.enroll(slug, principal.getUserId())));
    }

    @DeleteMapping("/{slug}/enroll")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Unenroll from a skill session (auth required)")
    public ResponseEntity<ApiResponse<Void>> unenroll(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        // principal is guaranteed non-null here — security config requires auth for DELETE
        skillSessionService.unenroll(slug, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Unenrolled from session"));
    }
}
