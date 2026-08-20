package com.major.CampuSphere.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_agenda")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "agenda_time", nullable = false, length = 50)
    private String time;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private int sortOrder = 0;
}
