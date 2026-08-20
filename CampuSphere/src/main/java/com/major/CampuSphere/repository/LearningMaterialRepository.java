package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.LearningMaterial;
import com.major.CampuSphere.enums.MaterialType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {

    Optional<LearningMaterial> findByMaterialKey(String materialKey);

    @Query("""
            SELECT m FROM LearningMaterial m
            WHERE (:semester IS NULL OR m.semester = :semester)
              AND (:subjectCode IS NULL OR m.subjectCode = :subjectCode)
              AND (:type IS NULL OR m.materialType = :type)
              AND (:q IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(m.author) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(m.subjectCode) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<LearningMaterial> search(@Param("semester") Integer semester,
                                   @Param("subjectCode") String subjectCode,
                                   @Param("type") MaterialType type,
                                   @Param("q") String q,
                                   Pageable pageable);

    List<LearningMaterial> findBySemesterAndSubjectCodeOrderByMaterialType(int semester, String subjectCode);
}
