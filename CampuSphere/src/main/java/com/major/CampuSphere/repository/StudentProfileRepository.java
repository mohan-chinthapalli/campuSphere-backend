package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    boolean existsByRollNumber(String rollNumber);
}
