package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "student_profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "roll_number", nullable = false, unique = true, length = 20)
    private String rollNumber;

    @Column(nullable = false, length = 100)
    private String branch;

    @Column(nullable = false, length = 50)
    private String year;

    @Column(nullable = false)
    @Builder.Default
    private int semester = 1;

    @Column(nullable = false, precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal cgpa = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal attendance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private int credits = 0;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
