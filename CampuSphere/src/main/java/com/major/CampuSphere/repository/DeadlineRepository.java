package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeadlineRepository extends JpaRepository<Deadline, Long> {
    List<Deadline> findByUserIdOrderByDueAtAsc(Long userId);
}
