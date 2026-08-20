package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.Announcement;
import com.major.CampuSphere.enums.AnnouncementTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("""
            SELECT a FROM Announcement a
            WHERE (:tag IS NULL OR a.tag = :tag)
              AND (:q IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(a.body) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY a.publishedAt DESC
            """)
    Page<Announcement> search(@Param("tag") AnnouncementTag tag,
                               @Param("q") String q,
                               Pageable pageable);
}
