package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.CampusPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CampusPlaceRepository extends JpaRepository<CampusPlace, Long> {
    Optional<CampusPlace> findBySlug(String slug);

    @Query("""
            SELECT p FROM CampusPlace p
            WHERE (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(p.type) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY p.name ASC
            """)
    List<CampusPlace> search(@Param("q") String q);
}
