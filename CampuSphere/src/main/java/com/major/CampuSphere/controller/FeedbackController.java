package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.FeedbackFacultyRequest;
import com.major.CampuSphere.dto.request.FeedbackPlatformRequest;
import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.FeedbackServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Platform feedback and anonymous faculty feedback")
public class FeedbackController {

    private final FeedbackServiceImpl feedbackService;

    @PostMapping("/platform")
    @Operation(summary = "Submit platform feedback")
    public ResponseEntity<ApiResponse<Void>> submitPlatform(
            @Valid @RequestBody FeedbackPlatformRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        feedbackService.submitPlatformFeedback(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Feedback submitted — thank you!"));
    }

    @PostMapping("/faculty")
    @Operation(summary = "Submit anonymous faculty feedback")
    public ResponseEntity<ApiResponse<Void>> submitFaculty(
            @Valid @RequestBody FeedbackFacultyRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        feedbackService.submitFacultyFeedback(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Faculty feedback submitted anonymously"));
    }
}
