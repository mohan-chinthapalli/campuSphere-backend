package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UpdateStudentProfileRequest {

    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String name;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    private List<String> skills;
}
