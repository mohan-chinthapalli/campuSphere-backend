package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.SessionEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionEnrollmentRepository extends JpaRepository<SessionEnrollment, Long> {
    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);
    Optional<SessionEnrollment> findBySessionIdAndUserId(Long sessionId, Long userId);
    List<SessionEnrollment> findByUserId(Long userId);
}
