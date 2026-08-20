package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.MentorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("""
            SELECT mp FROM MentorProfile mp
            JOIN mp.user u
            LEFT JOIN StudentProfile sp ON sp.user.id = u.id
            LEFT JOIN StudentSkill sk ON sk.user.id = u.id
            WHERE (:q IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :q, '%'))
                             OR LOWER(mp.headline) LIKE LOWER(CONCAT('%', :q, '%')))
              AND (:skill IS NULL OR LOWER(sk.skill) LIKE LOWER(CONCAT('%', :skill, '%')))
              AND (:branch IS NULL OR LOWER(sp.branch) LIKE LOWER(CONCAT('%', :branch, '%')))
            """)
    Page<MentorProfile> searchMentors(@Param("q") String q,
                                       @Param("skill") String skill,
                                       @Param("branch") String branch,
                                       Pageable pageable);
}
