package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.ClubResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.ClubServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
@Tag(name = "Clubs", description = "Club listing, details and membership")
public class ClubController {

    private final ClubServiceImpl clubService;

    @GetMapping
    @Operation(summary = "List/search clubs")
    public ResponseEntity<ApiResponse<PageResponse<ClubResponse>>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return ResponseEntity.ok(ApiResponse.success("Clubs retrieved",
                clubService.listClubs(category, q, userId, pageable)));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get club by slug")
    public ResponseEntity<ApiResponse<ClubResponse>> getBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.success(clubService.getBySlug(slug, userId)));
    }

    @PostMapping("/{slug}/join")
    @Operation(summary = "Join a club")
    public ResponseEntity<ApiResponse<ClubResponse>> join(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Joined club",
                clubService.join(slug, principal.getUserId())));
    }

    @DeleteMapping("/{slug}/join")
    @Operation(summary = "Leave a club")
    public ResponseEntity<ApiResponse<ClubResponse>> leave(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Left club",
                clubService.leave(slug, principal.getUserId())));
    }
}
