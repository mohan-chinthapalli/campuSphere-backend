package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timetable_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 255)
    private String courseName;

    @Column(name = "class_time", length = 100)
    private String classTime;

    @Column(length = 50)
    private String room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private User faculty;

    @Column(name = "day_of_week", nullable = false)
    private int dayOfWeek;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
