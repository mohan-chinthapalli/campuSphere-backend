package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.MentorshipRequest;
import com.major.CampuSphere.enums.MentorshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, Long> {
    List<MentorshipRequest> findByMenteeIdOrderByRequestedAtDesc(Long menteeId);
    List<MentorshipRequest> findByMentorIdOrderByRequestedAtDesc(Long mentorId);
    boolean existsByMentorIdAndMenteeIdAndStatus(Long mentorId, Long menteeId, MentorshipStatus status);
}
