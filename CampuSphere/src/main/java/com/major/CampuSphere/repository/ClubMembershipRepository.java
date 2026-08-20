package com.major.CampuSphere.repository;

import com.major.CampuSphere.entity.ClubMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {
    boolean existsByClubIdAndUserId(Long clubId, Long userId);
    Optional<ClubMembership> findByClubIdAndUserId(Long clubId, Long userId);
    List<ClubMembership> findByUserId(Long userId);
}
