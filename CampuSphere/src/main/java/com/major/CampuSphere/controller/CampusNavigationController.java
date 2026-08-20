package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.CampusPlaceResponse;
import com.major.CampuSphere.service.impl.CampusNavigationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Tag(name = "Campus Navigation", description = "Campus map places and locations")
public class CampusNavigationController {

    private final CampusNavigationServiceImpl navigationService;

    @GetMapping
    @Operation(summary = "List/search campus places")
    public ResponseEntity<ApiResponse<List<CampusPlaceResponse>>> list(
            @RequestParam(required = false) String q) {

        return ResponseEntity.ok(ApiResponse.success("Places retrieved",
                navigationService.listPlaces(q)));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get campus place by slug")
    public ResponseEntity<ApiResponse<CampusPlaceResponse>> getBySlug(
            @PathVariable String slug) {

        return ResponseEntity.ok(ApiResponse.success(
                navigationService.getBySlug(slug)));
    }
}
