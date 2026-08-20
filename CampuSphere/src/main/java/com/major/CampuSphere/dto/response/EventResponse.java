package com.major.CampuSphere.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {
    private Long id;
    private String slug;
    private String title;
    private String category;
    private String tagline;
    private String description;
    private String prize;
    private String eventDate;
    private Instant startsAt;
    private String displayTime;
    private String venue;
    private int seatsTotal;
    private int seatsLeft;
    private int participants;
    private String gradient;
    private String emoji;
    private List<String> tags;
    private OrganizerInfo organizer;
    private List<AgendaItem> agenda;
    private boolean registeredByCurrentUser;
    private Instant createdAt;

    @Getter @Builder
    public static class OrganizerInfo {
        private String name;
        private String club;
        private String email;
    }

    @Getter @Builder
    public static class AgendaItem {
        private String time;
        private String title;
    }
}
