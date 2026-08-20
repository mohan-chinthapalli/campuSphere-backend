package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.response.DashboardResponse;
import com.major.CampuSphere.dto.response.DeadlineResponse;
import com.major.CampuSphere.dto.response.TimetableResponse;
import com.major.CampuSphere.entity.*;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicsServiceImpl {

    private final UserRepository userRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final TimetableEntryRepository timetableRepo;
    private final DeadlineRepository deadlineRepo;
    private final NotificationServiceImpl notificationService;
    private final EventServiceImpl eventService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        StudentProfile sp = studentProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile for user", userId));

        int todayDow = LocalDate.now().getDayOfWeek().getValue(); // 1=Monday
        List<TimetableResponse> todayClasses = timetableRepo
                .findByUserIdAndDayOfWeekOrderBySortOrder(userId, todayDow)
                .stream().map(this::toTimetableResponse).toList();

        List<DeadlineResponse> deadlines = deadlineRepo
                .findByUserIdOrderByDueAtAsc(userId)
                .stream().limit(5).map(this::toDeadlineResponse).toList();

        var upcomingEvents = eventService
                .listEvents(null, null, userId, PageRequest.of(0, 3))
                .getContent();

        var notifications = notificationService
                .list(userId, PageRequest.of(0, 5))
                .getContent();

        long unread = notificationService.unreadCount(userId);

        String initials = buildInitials(user.getName());

        return DashboardResponse.builder()
                .student(DashboardResponse.StudentSummary.builder()
                        .name(user.getName())
                        .initials(initials)
                        .rollNumber(sp.getRollNumber())
                        .branch(sp.getBranch())
                        .year(sp.getYear())
                        .cgpa(sp.getCgpa())
                        .attendance(sp.getAttendance())
                        .credits(sp.getCredits())
                        .build())
                .todayClasses(todayClasses)
                .upcomingDeadlines(deadlines)
                .upcomingEvents(upcomingEvents)
                .recentNotifications(notifications)
                .unreadNotifications(unread)
                .build();
    }

    @Transactional(readOnly = true)
    public List<TimetableResponse> getTimetable(Long userId) {
        return timetableRepo.findByUserIdOrderByDayOfWeekAscSortOrderAsc(userId)
                .stream().map(this::toTimetableResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DeadlineResponse> getDeadlines(Long userId) {
        return deadlineRepo.findByUserIdOrderByDueAtAsc(userId)
                .stream().map(this::toDeadlineResponse).toList();
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private TimetableResponse toTimetableResponse(TimetableEntry e) {
        return TimetableResponse.builder()
                .id(e.getId())
                .courseCode(e.getCourseCode())
                .courseName(e.getCourseName())
                .classTime(e.getClassTime())
                .room(e.getRoom())
                .facultyName(e.getFaculty() != null ? e.getFaculty().getName() : null)
                .dayOfWeek(e.getDayOfWeek())
                .sortOrder(e.getSortOrder())
                .build();
    }

    private DeadlineResponse toDeadlineResponse(Deadline d) {
        return DeadlineResponse.builder()
                .id(d.getId())
                .title(d.getTitle())
                .courseCode(d.getCourseCode())
                .dueAt(d.getDueAt())
                .urgency(d.getUrgency())
                .build();
    }

    private String buildInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
}
