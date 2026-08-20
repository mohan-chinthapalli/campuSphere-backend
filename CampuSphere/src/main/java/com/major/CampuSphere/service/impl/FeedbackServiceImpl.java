package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.FeedbackFacultyRequest;
import com.major.CampuSphere.dto.request.FeedbackPlatformRequest;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl {

    private final UserRepository userRepo;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void submitPlatformFeedback(FeedbackPlatformRequest request, Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        jdbcTemplate.update(
            "INSERT INTO feedback_platform (user_id, topic, rating, subject, details) VALUES (?,?,?,?,?)",
            userId, request.getTopic(), request.getRating(),
            request.getSubject(), request.getDetails());

        log.info("Platform feedback submitted by userId={}", userId);
    }

    @Transactional
    public void submitFacultyFeedback(FeedbackFacultyRequest request, Long submitterId) {
        // Verify faculty exists
        User faculty = userRepo.findById(request.getFacultyUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty user", request.getFacultyUserId()));

        // NOTE: No student FK stored — feedback is anonymous per product requirements
        jdbcTemplate.update(
            "INSERT INTO feedback_faculty " +
            "(faculty_id, course_code, semester, clarity_score, pace_score, support_score, fairness_score, comments) " +
            "VALUES (?,?,?,?,?,?,?,?)",
            faculty.getId(),
            request.getCourseCode(),
            request.getSemester(),
            request.getClarityScore(),
            request.getPaceScore(),
            request.getSupportScore(),
            request.getFairnessScore(),
            request.getComments());

        log.info("Faculty feedback submitted for facultyId={} (anonymous)", faculty.getId());
    }
}
