package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.UpdateProgressRequest;
import com.major.CampuSphere.dto.response.*;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.LearningHubServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Learning Hub", description = "Subjects, materials, progress and bookmarks")
public class LearningHubController {

    private final LearningHubServiceImpl learningHubService;

    // ─── Subjects ─────────────────────────────────────────────────

    @GetMapping("/api/subjects")
    @Operation(summary = "List all subjects, optionally filtered by semester")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> listSubjects(
            @RequestParam(required = false) Integer semester) {

        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved",
                learningHubService.listSubjects(semester)));
    }

    // ─── Materials ────────────────────────────────────────────────

    @GetMapping("/api/materials")
    @Operation(summary = "List/search learning materials")
    public ResponseEntity<ApiResponse<PageResponse<MaterialResponse>>> listMaterials(
            @RequestParam(required = false) Integer semester,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("semester", "subjectCode"));
        return ResponseEntity.ok(ApiResponse.success("Materials retrieved",
                learningHubService.listMaterials(semester, subject, type, q, userId, pageable)));
    }

    @GetMapping("/api/materials/{id}")
    @Operation(summary = "Get material by numeric ID")
    public ResponseEntity<ApiResponse<MaterialResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.success(learningHubService.getById(id, userId)));
    }

    @GetMapping("/api/materials/key/{materialKey}")
    @Operation(summary = "Get material by frontend key (e.g. m-6-CS601-NOTES)")
    public ResponseEntity<ApiResponse<MaterialResponse>> getByKey(
            @PathVariable String materialKey,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.success(learningHubService.getByKey(materialKey, userId)));
    }

    @GetMapping("/api/materials/bookmarked")
    @Operation(summary = "Get bookmarked materials for current user")
    public ResponseEntity<ApiResponse<List<MaterialResponse>>> getBookmarked(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Bookmarked materials retrieved",
                learningHubService.getBookmarked(principal.getUserId())));
    }

    // ─── Progress & Bookmarks ─────────────────────────────────────

    @PatchMapping("/api/materials/{id}/progress")
    @Operation(summary = "Update reading progress and/or bookmark status")
    public ResponseEntity<ApiResponse<MaterialResponse>> updateProgress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Progress updated",
                learningHubService.updateProgress(id, request, principal.getUserId())));
    }

    @PostMapping("/api/materials/{id}/bookmark")
    @Operation(summary = "Toggle bookmark on a material")
    public ResponseEntity<ApiResponse<MaterialResponse>> toggleBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Bookmark toggled",
                learningHubService.toggleBookmark(id, principal.getUserId())));
    }

    @PostMapping("/api/materials/{id}/download")
    @Operation(summary = "Record a download (increments counter)")
    public ResponseEntity<ApiResponse<MaterialResponse>> download(
            @PathVariable Long id,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.success("Download recorded",
                learningHubService.recordDownload(id, userId)));
    }
}
