package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(length = 500)
    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String prize;

    @Column(name = "event_date", length = 100)
    private String eventDate;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "display_time", length = 100)
    private String displayTime;

    @Column(length = 255)
    private String venue;

    @Column(name = "seats_total", nullable = false)
    @Builder.Default
    private int seatsTotal = 0;

    @Column(name = "organizer_name", length = 100)
    private String organizerName;

    @Column(name = "organizer_club", length = 100)
    private String organizerClub;

    @Column(name = "organizer_email", length = 255)
    private String organizerEmail;

    @Column(length = 255)
    private String gradient;

    @Column(length = 10)
    private String emoji;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<EventAgenda> agenda = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventRegistration> registrations = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public int getSeatsLeft() {
        return Math.max(0, seatsTotal - registrations.size());
    }

    public int getParticipantsCount() {
        return registrations.size();
    }
}
