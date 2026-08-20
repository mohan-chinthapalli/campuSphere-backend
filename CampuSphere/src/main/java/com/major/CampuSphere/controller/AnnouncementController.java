package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.CreateAnnouncementRequest;
import com.major.CampuSphere.dto.response.AnnouncementResponse;
import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.AnnouncementServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Announcements", description = "Campus announcements feed")
public class AnnouncementController {

    private final AnnouncementServiceImpl announcementService;

    @GetMapping
    @Operation(summary = "List/search announcements")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> list(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved",
                announcementService.list(tag, q, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get announcement by ID")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    @Operation(summary = "Create announcement (FACULTY/ADMIN only)")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @Valid @RequestBody CreateAnnouncementRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Announcement created",
                        announcementService.create(request, principal.getUserId())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete announcement (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Announcement deleted"));
    }
}
