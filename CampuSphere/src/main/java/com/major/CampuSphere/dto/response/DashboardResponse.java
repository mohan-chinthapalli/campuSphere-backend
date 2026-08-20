package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class DashboardResponse {
    private StudentSummary student;
    private List<TimetableResponse> todayClasses;
    private List<DeadlineResponse> upcomingDeadlines;
    private List<EventResponse> upcomingEvents;
    private List<NotificationResponse> recentNotifications;
    private long unreadNotifications;

    @Getter @Builder
    public static class StudentSummary {
        private String name;
        private String initials;
        private String rollNumber;
        private String branch;
        private String year;
        private BigDecimal cgpa;
        private BigDecimal attendance;
        private int credits;
    }
}
