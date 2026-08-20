package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class AiResponse {
    private String conversationId;
    private String answer;          // used by doubt panel
    private String reply;           // used by general chat
    private List<String> sources;
    private boolean demo;
}
