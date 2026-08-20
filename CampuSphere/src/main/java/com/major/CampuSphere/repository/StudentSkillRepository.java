package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndSkill(Long userId, String skill);
}
