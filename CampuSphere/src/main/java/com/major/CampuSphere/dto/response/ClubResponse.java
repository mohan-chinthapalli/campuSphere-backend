package com.major.CampuSphere.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubResponse {
    private Long id;
    private String slug;
    private String name;
    private String category;
    private String tagline;
    private String about;
    private String mission;
    private int memberCount;
    private boolean recruiting;
    private String gradient;
    private String emoji;
    private String coordinatorName;
    private List<LeadInfo> leads;
    private List<String> achievements;
    private boolean memberOfCurrentUser;

    @Getter @Builder
    public static class LeadInfo {
        private String name;
        private String role;
    }
}
