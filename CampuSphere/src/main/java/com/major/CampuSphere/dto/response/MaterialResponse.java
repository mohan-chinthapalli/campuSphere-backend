package com.major.CampuSphere.dto.response;

import com.major.CampuSphere.enums.MaterialType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter @Builder
public class MaterialResponse {
    private Long id;
    private String materialKey;
    private String title;
    private MaterialType materialType;
    private String subjectCode;
    private int semester;
    private String author;
    private String fileSize;
    private int downloadCount;
    private String emoji;
    private String gradient;
    // Per-user fields (null if not authenticated or no record)
    private Integer progressPct;
    private boolean bookmarked;
    private Instant lastReadAt;
    private Instant createdAt;
}
