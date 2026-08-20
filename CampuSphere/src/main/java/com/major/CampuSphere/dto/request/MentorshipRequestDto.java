package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MentorshipRequestDto {

    @NotNull(message = "Mentor user ID is required")
    private Long mentorUserId;

    private String message;
}
