package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.response.NotificationResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.entity.Notification;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.NotificationType;
import com.major.CampuSphere.exception.ForbiddenException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.NotificationRepository;
import com.major.CampuSphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepo
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepo.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(Long notificationId, Long userId) {
        Notification n = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));

        if (!n.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
        n.setRead(true);
        return toResponse(notificationRepo.save(n));
    }

    @Transactional
    public int markAllRead(Long userId) {
        return notificationRepo.markAllReadByUserId(userId);
    }

    @Transactional
    public void delete(Long notificationId, Long userId) {
        Notification n = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        if (!n.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
        notificationRepo.delete(n);
    }

    // Used internally by other services to push notifications
    @Transactional
    public void push(Long userId, String title, String body,
                     NotificationType type, Long referenceId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Notification n = Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .notificationType(type)
                .referenceId(referenceId)
                .build();
        notificationRepo.save(n);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .body(n.getBody())
                .notificationType(n.getNotificationType())
                .referenceId(n.getReferenceId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
