package com.major.CampuSphere.dto.response;

import com.major.CampuSphere.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter @Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String body;
    private NotificationType notificationType;
    private Long referenceId;
    private boolean read;
    private Instant createdAt;
}
