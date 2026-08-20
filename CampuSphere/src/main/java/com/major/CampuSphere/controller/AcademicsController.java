package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.response.*;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.AcademicsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academics")
@RequiredArgsConstructor
@Tag(name = "Academics", description = "Student academic data — dashboard, timetable, deadlines")
public class AcademicsController {

    private final AcademicsServiceImpl academicsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get full student dashboard data")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved",
                academicsService.getDashboard(principal.getUserId())));
    }

    @GetMapping("/timetable")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get student timetable (all days)")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getTimetable(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Timetable retrieved",
                academicsService.getTimetable(principal.getUserId())));
    }

    @GetMapping("/deadlines")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get upcoming deadlines for the student")
    public ResponseEntity<ApiResponse<List<DeadlineResponse>>> getDeadlines(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Deadlines retrieved",
                academicsService.getDeadlines(principal.getUserId())));
    }
}
