package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "skill_session_outcomes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SkillSessionOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private SkillSession session;

    @Column(nullable = false, length = 500)
    private String outcome;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
