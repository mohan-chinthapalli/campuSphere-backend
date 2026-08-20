package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.dto.response.SkillSessionResponse;
import com.major.CampuSphere.entity.SessionEnrollment;
import com.major.CampuSphere.entity.SkillSession;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.exception.DuplicateResourceException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.SessionEnrollmentRepository;
import com.major.CampuSphere.repository.SkillSessionRepository;
import com.major.CampuSphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillSessionServiceImpl {

    private final SkillSessionRepository sessionRepo;
    private final SessionEnrollmentRepository enrollmentRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public PageResponse<SkillSessionResponse> list(String category, String q,
                                                    Long currentUserId, Pageable pageable) {
        Page<SkillSession> page = sessionRepo.search(
                (category != null && category.isBlank()) ? null : category,
                (q != null && q.isBlank()) ? null : q,
                pageable);
        return PageResponse.from(page.map(s -> toResponse(s, currentUserId)));
    }

    @Transactional(readOnly = true)
    public SkillSessionResponse getBySlug(String slug, Long currentUserId) {
        SkillSession session = sessionRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Skill session", slug));
        return toResponse(session, currentUserId);
    }

    @Transactional
    public SkillSessionResponse enroll(String slug, Long userId) {
        SkillSession session = sessionRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Skill session", slug));

        if (enrollmentRepo.existsBySessionIdAndUserId(session.getId(), userId)) {
            throw new DuplicateResourceException("You are already enrolled in this session");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        SessionEnrollment enrollment = SessionEnrollment.builder()
                .session(session)
                .user(user)
                .build();
        enrollmentRepo.save(enrollment);

        session.setEnrolledCount(session.getEnrolledCount() + 1);
        sessionRepo.save(session);

        log.info("User {} enrolled in session {}", userId, slug);
        return getBySlug(slug, userId);
    }

    @Transactional
    public void unenroll(String slug, Long userId) {
        SkillSession session = sessionRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Skill session", slug));

        SessionEnrollment enrollment = enrollmentRepo
                .findBySessionIdAndUserId(session.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        enrollmentRepo.delete(enrollment);
        session.setEnrolledCount(Math.max(0, session.getEnrolledCount() - 1));
        sessionRepo.save(session);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private SkillSessionResponse toResponse(SkillSession s, Long currentUserId) {
        boolean enrolled = currentUserId != null &&
                enrollmentRepo.existsBySessionIdAndUserId(s.getId(), currentUserId);

        Integer completedSessions = null;
        if (enrolled) {
            completedSessions = enrollmentRepo
                    .findBySessionIdAndUserId(s.getId(), currentUserId)
                    .map(SessionEnrollment::getCompletedSessions)
                    .orElse(0);
        }

        List<String> outcomes = s.getOutcomes().stream()
                .map(o -> o.getOutcome())
                .toList();

        return SkillSessionResponse.builder()
                .id(s.getId())
                .slug(s.getSlug())
                .title(s.getTitle())
                .facultyName(s.getFaculty() != null ? s.getFaculty().getName() : null)
                .department(s.getDepartment())
                .category(s.getCategory())
                .level(s.getLevel())
                .totalSessions(s.getTotalSessions())
                .duration(s.getDuration())
                .enrolledCount(s.getEnrolledCount())
                .rating(s.getRating())
                .schedule(s.getSchedule())
                .emoji(s.getEmoji())
                .gradient(s.getGradient())
                .outcomes(outcomes)
                .enrolledByCurrentUser(enrolled)
                .completedSessions(completedSessions)
                .build();
    }
}
