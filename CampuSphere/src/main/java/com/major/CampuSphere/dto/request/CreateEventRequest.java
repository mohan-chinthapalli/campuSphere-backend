package com.major.CampuSphere.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter @Setter
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    private String tagline;
    private String description;
    private String prize;
    private String eventDate;
    private Instant startsAt;
    private String displayTime;

    @NotBlank(message = "Venue is required")
    private String venue;

    @NotNull @Positive(message = "Seats must be positive")
    private Integer seatsTotal;

    private String organizerName;
    private String organizerClub;
    private String organizerEmail;
    private String gradient;
    private String emoji;
    private List<String> tags;
    private List<AgendaItemRequest> agenda;

    @Getter @Setter
    public static class AgendaItemRequest {
        private String time;
        private String title;
    }
}
