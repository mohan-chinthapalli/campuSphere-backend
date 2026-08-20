package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_skills")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String skill;
}
