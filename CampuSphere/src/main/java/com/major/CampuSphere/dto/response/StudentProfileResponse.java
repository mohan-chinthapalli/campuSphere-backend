package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class StudentProfileResponse {
    private Long userId;
    private String name;
    private String email;
    private String rollNumber;
    private String branch;
    private String year;
    private int semester;
    private BigDecimal cgpa;
    private BigDecimal attendance;
    private int credits;
    private String bio;
    private List<String> skills;
    private String initials;
}
