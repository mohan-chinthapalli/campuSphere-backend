package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.EventDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDiscussionRepository extends JpaRepository<EventDiscussion, Long> {
    List<EventDiscussion> findByEventIdOrderByCreatedAtDesc(Long eventId);
}
