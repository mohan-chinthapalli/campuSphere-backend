package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class MentorResponse {
    private Long userId;
    private String name;
    private String year;
    private String branch;
    private String headline;
    private List<String> skills;
    private BigDecimal avgRating;
    private int totalSessions;
    private String responseTime;
    private boolean available;
    private String emoji;
}
