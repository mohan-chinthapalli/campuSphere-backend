package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.MaterialProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaterialProgressRepository extends JpaRepository<MaterialProgress, Long> {
    Optional<MaterialProgress> findByMaterialIdAndUserId(Long materialId, Long userId);
    List<MaterialProgress> findByUserId(Long userId);
    List<MaterialProgress> findByUserIdAndBookmarkedTrue(Long userId);
}
