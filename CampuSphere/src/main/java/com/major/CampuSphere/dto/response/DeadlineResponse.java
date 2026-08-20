package com.major.CampuSphere.dto.response;

import com.major.CampuSphere.enums.Urgency;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter @Builder
public class DeadlineResponse {
    private Long id;
    private String title;
    private String courseCode;
    private Instant dueAt;
    private Urgency urgency;
}
