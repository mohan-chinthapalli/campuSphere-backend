package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class SubjectResponse {
    private Long id;
    private String code;
    private String name;
    private String emoji;
    private int semester;
    private String facultyName;
}
