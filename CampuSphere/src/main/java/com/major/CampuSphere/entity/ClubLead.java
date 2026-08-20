package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club_leads")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClubLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String role;
}
