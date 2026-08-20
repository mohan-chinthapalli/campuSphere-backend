package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "campus_places")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CampusPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    @Builder.Default
    private int floors = 1;

    @Column(name = "open_hours", length = 100)
    private String openHours;

    @Column(name = "map_x", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal mapX = BigDecimal.ZERO;

    @Column(name = "map_y", nullable = false, precision = 6, scale = 2)
    @Builder.Default
    private BigDecimal mapY = BigDecimal.ZERO;

    @Column(name = "walk_time", length = 50)
    private String walkTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
