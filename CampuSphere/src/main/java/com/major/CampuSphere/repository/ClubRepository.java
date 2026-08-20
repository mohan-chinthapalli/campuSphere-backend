package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT c FROM Club c
            WHERE (:category IS NULL OR LOWER(c.category) = LOWER(:category))
              AND (:q IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(c.tagline) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Club> searchClubs(@Param("category") String category,
                           @Param("q") String q,
                           Pageable pageable);
}
