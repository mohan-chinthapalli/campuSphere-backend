package com.major.CampuSphere.controller;

import com.major.CampuSphere.dto.response.ApiResponse;
import com.major.CampuSphere.dto.response.NotificationResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.security.CampuSpherePrincipal;
import com.major.CampuSphere.service.impl.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notification management")
public class NotificationController {

    private final NotificationServiceImpl notificationService;

    @GetMapping
    @Operation(summary = "List notifications for current user")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved",
                notificationService.list(principal.getUserId(), PageRequest.of(page, size))));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        long count = notificationService.unreadCount(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark single notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        return ResponseEntity.ok(ApiResponse.success("Notification marked as read",
                notificationService.markRead(id, principal.getUserId())));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllRead(
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        int updated = notificationService.markAllRead(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("updatedCount", updated)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CampuSpherePrincipal principal) {

        notificationService.delete(id, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ok("Notification deleted"));
    }
}
