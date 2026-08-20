package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.SkillSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SkillSessionRepository extends JpaRepository<SkillSession, Long> {

    Optional<SkillSession> findBySlug(String slug);

    @Query("""
            SELECT s FROM SkillSession s
            WHERE (:category IS NULL OR LOWER(s.category) = LOWER(:category))
              AND (:q IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<SkillSession> search(@Param("category") String category,
                               @Param("q") String q,
                               Pageable pageable);
}
