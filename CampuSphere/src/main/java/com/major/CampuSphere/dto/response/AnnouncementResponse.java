package com.major.CampuSphere.dto.response;

import com.major.CampuSphere.enums.AnnouncementTag;
import com.major.CampuSphere.enums.Priority;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter @Builder
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String body;
    private String author;
    private AnnouncementTag tag;
    private Priority priority;
    private Instant publishedAt;
}
