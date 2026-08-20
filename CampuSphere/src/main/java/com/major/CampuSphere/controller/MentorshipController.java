package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.MentorshipRequestDto;
import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.MentorResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.MentorshipServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentors")
@RequiredArgsConstructor
@Tag(name = "Mentorship", description = "Mentor discovery and session requests")
public class MentorshipController {

    private final MentorshipServiceImpl mentorshipService;

    @GetMapping
    @Operation(summary = "List/search available mentors")
    public ResponseEntity<ApiResponse<PageResponse<MentorResponse>>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String branch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("avgRating").descending());
        return ResponseEntity.ok(ApiResponse.success("Mentors retrieved",
                mentorshipService.listMentors(q, skill, branch, pageable)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get mentor profile by user ID")
    public ResponseEntity<ApiResponse<MentorResponse>> getByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(ApiResponse.success(
                mentorshipService.getMentorByUserId(userId)));
    }

    @PostMapping("/request")
    @Operation(summary = "Send a mentorship request")
    public ResponseEntity<ApiResponse<Void>> request(
            @Valid @RequestBody MentorshipRequestDto request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        mentorshipService.requestMentorship(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Mentorship request sent"));
    }

    @PatchMapping("/requests/{requestId}/respond")
    @Operation(summary = "Accept or reject a mentorship request (mentor only)")
    public ResponseEntity<ApiResponse<Void>> respond(
            @PathVariable Long requestId,
            @RequestParam String action,   // "ACCEPT" or "REJECT"
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        mentorshipService.respondToRequest(requestId, action, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Response recorded"));
    }
}
