package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.response.ClubResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.entity.Club;
import com.major.CampuSphere.entity.ClubMembership;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.exception.BadRequestException;
import com.major.CampuSphere.exception.DuplicateResourceException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.ClubMembershipRepository;
import com.major.CampuSphere.repository.ClubRepository;
import com.major.CampuSphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubServiceImpl {

    private final ClubRepository clubRepo;
    private final ClubMembershipRepository membershipRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public PageResponse<ClubResponse> listClubs(String category, String q, Long currentUserId, Pageable pageable) {
        Page<Club> page = clubRepo.searchClubs(
                (category != null && category.isBlank()) ? null : category,
                (q != null && q.isBlank()) ? null : q,
                pageable);
        return PageResponse.from(page.map(c -> toResponse(c, currentUserId)));
    }

    @Transactional(readOnly = true)
    public ClubResponse getBySlug(String slug, Long currentUserId) {
        Club club = clubRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Club", slug));
        return toResponse(club, currentUserId);
    }

    @Transactional
    public ClubResponse join(String slug, Long userId) {
        Club club = clubRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Club", slug));

        if (membershipRepo.existsByClubIdAndUserId(club.getId(), userId)) {
            throw new DuplicateResourceException("You are already a member of this club");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        ClubMembership membership = ClubMembership.builder()
                .club(club)
                .user(user)
                .build();
        membershipRepo.save(membership);

        // Update denormalised count
        club.setMemberCount(club.getMemberCount() + 1);
        clubRepo.save(club);

        log.info("User {} joined club {}", userId, slug);
        return getBySlug(slug, userId);
    }

    @Transactional
    public ClubResponse leave(String slug, Long userId) {
        Club club = clubRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Club", slug));

        ClubMembership membership = membershipRepo.findByClubIdAndUserId(club.getId(), userId)
                .orElseThrow(() -> new BadRequestException("You are not a member of this club"));

        membershipRepo.delete(membership);

        club.setMemberCount(Math.max(0, club.getMemberCount() - 1));
        clubRepo.save(club);

        log.info("User {} left club {}", userId, slug);
        return getBySlug(slug, userId);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private ClubResponse toResponse(Club c, Long currentUserId) {
        boolean isMember = currentUserId != null &&
                membershipRepo.existsByClubIdAndUserId(c.getId(), currentUserId);

        List<ClubResponse.LeadInfo> leads = c.getLeads().stream()
                .map(l -> ClubResponse.LeadInfo.builder()
                        .name(l.getName())
                        .role(l.getRole())
                        .build())
                .toList();

        List<String> achievements = c.getAchievements().stream()
                .map(a -> a.getAchievement())
                .toList();

        return ClubResponse.builder()
                .id(c.getId())
                .slug(c.getSlug())
                .name(c.getName())
                .category(c.getCategory())
                .tagline(c.getTagline())
                .about(c.getAbout())
                .mission(c.getMission())
                .memberCount(c.getMemberCount())
                .recruiting(c.isRecruiting())
                .gradient(c.getGradient())
                .emoji(c.getEmoji())
                .coordinatorName(c.getCoordinator() != null ? c.getCoordinator().getName() : null)
                .leads(leads)
                .achievements(achievements)
                .memberOfCurrentUser(isMember)
                .build();
    }
}
