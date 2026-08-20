package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateFacultyProfileRequest {

    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String name;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    private String office;
    private String officeHours;
}
