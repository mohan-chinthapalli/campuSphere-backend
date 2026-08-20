package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "material_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"material_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaterialProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private LearningMaterial material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "progress_pct", nullable = false)
    @Builder.Default
    private int progressPct = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean bookmarked = false;

    @Column(name = "last_read_at")
    private Instant lastReadAt;
}
