package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "session_enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "user_id"}))
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SkillSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "completed_sessions", nullable = false)
    @Builder.Default
    private int completedSessions = 0;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @CreatedDate
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;
}
