package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class TimetableResponse {
    private Long id;
    private String courseCode;
    private String courseName;
    private String classTime;
    private String room;
    private String facultyName;
    private int dayOfWeek;
    private int sortOrder;
}
