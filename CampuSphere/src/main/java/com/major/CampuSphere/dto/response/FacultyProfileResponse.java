package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class FacultyProfileResponse {
    private Long userId;
    private String name;
    private String email;
    private String title;
    private String department;
    private String office;
    private String officeHours;
    private String bio;
    private int publications;
    private int citations;
    private int studentCount;
    private String initials;
}
