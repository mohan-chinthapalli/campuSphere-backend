package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.request.CreateEventRequest;
import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.EventResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.EventServiceImpl;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event listing, details and registration")
public class EventController {

    private final EventServiceImpl eventService;

    @GetMapping
    @Operation(summary = "List/search events")
    public ResponseEntity<ApiResponse<PageResponse<EventResponse>>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("startsAt").ascending());
        return ResponseEntity.ok(ApiResponse.success("Events retrieved",
                eventService.listEvents(category, q, userId, pageable)));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get event by slug")
    public ResponseEntity<ApiResponse<EventResponse>> getBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        Long userId = principal != null ? principal.getUserId() : null;
        return ResponseEntity.ok(ApiResponse.success(eventService.getBySlug(slug, userId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    @Operation(summary = "Create event (FACULTY/ADMIN only)")
    public ResponseEntity<ApiResponse<EventResponse>> create(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Event created",
                        eventService.create(request, principal.getUserId())));
    }

    @PostMapping("/{slug}/register")
    @Operation(summary = "Register current user for an event")
    public ResponseEntity<ApiResponse<EventResponse>> register(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Registered for event",
                eventService.register(slug, principal.getUserId())));
    }

    @DeleteMapping("/{slug}/register")
    @Operation(summary = "Cancel event registration")
    public ResponseEntity<ApiResponse<Void>> unregister(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        eventService.unregister(slug, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Registration cancelled"));
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasAnyRole('FACULTY','ADMIN')")
    @Operation(summary = "Delete event (creator or ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String slug,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        eventService.delete(slug, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Event deleted"));
    }
}
