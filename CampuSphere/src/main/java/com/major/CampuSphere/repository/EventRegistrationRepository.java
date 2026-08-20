package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);
    long countByEventId(Long eventId);
}
