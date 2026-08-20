package com.major.CampuSphere.dto.request;

import com.major.CampuSphere.enums.AnnouncementTag;
import com.major.CampuSphere.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateAnnouncementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body is required")
    private String body;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Tag is required")
    private AnnouncementTag tag;

    private Priority priority = Priority.NORMAL;
}
