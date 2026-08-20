package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FeedbackFacultyRequest {

    @NotNull(message = "Faculty user ID is required")
    private Long facultyUserId;

    private String courseCode;
    private Integer semester;

    @NotNull @Min(1) @Max(5)
    private Integer clarityScore;

    @NotNull @Min(1) @Max(5)
    private Integer paceScore;

    @NotNull @Min(1) @Max(5)
    private Integer supportScore;

    @NotNull @Min(1) @Max(5)
    private Integer fairnessScore;

    private String comments;
}
