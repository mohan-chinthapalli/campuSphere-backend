package com.major.CampuSphere.dto.response;

import com.major.CampuSphere.enums.SessionLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class SkillSessionResponse {
    private Long id;
    private String slug;
    private String title;
    private String facultyName;
    private String department;
    private String category;
    private SessionLevel level;
    private int totalSessions;
    private String duration;
    private int enrolledCount;
    private BigDecimal rating;
    private String schedule;
    private String emoji;
    private String gradient;
    private List<String> outcomes;
    private boolean enrolledByCurrentUser;
    private Integer completedSessions;
}
