package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT e FROM Event e
            WHERE (:category = '' OR LOWER(e.category) = LOWER(:category))
              AND (:q = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(e.tagline) LIKE LOWER(CONCAT('%', :q, '%'))
                           OR LOWER(e.venue) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY e.startsAt ASC
            """)
    Page<Event> searchEvents(@Param("category") String category,
                             @Param("q") String q,
                             Pageable pageable);

    long count();
}
