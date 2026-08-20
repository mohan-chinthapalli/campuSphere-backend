package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.MentorshipRequestDto;
import com.major.CampuSphere.dto.response.MentorResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.entity.MentorProfile;
import com.major.CampuSphere.entity.MentorshipRequest;
import com.major.CampuSphere.entity.StudentProfile;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.MentorshipStatus;
import com.major.CampuSphere.exception.BadRequestException;
import com.major.CampuSphere.exception.DuplicateResourceException;
import com.major.CampuSphere.exception.ForbiddenException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl {

    private final MentorProfileRepository mentorProfileRepo;
    private final MentorshipRequestRepository requestRepo;
    private final StudentProfileRepository studentProfileRepo;
    private final StudentSkillRepository skillRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public PageResponse<MentorResponse> listMentors(String q, String skill, String branch,
                                                     Pageable pageable) {
        Page<MentorProfile> page = mentorProfileRepo.searchMentors(
                (q != null && q.isBlank()) ? null : q,
                (skill != null && skill.isBlank()) ? null : skill,
                (branch != null && branch.isBlank()) ? null : branch,
                pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public MentorResponse getMentorByUserId(Long userId) {
        MentorProfile profile = mentorProfileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor profile", userId));
        return toResponse(profile);
    }

    @Transactional
    public void requestMentorship(MentorshipRequestDto request, Long menteeId) {
        User mentor = userRepo.findById(request.getMentorUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getMentorUserId()));

        if (!mentorProfileRepo.existsByUserId(mentor.getId())) {
            throw new BadRequestException("This user is not a registered mentor");
        }

        if (requestRepo.existsByMentorIdAndMenteeIdAndStatus(
                mentor.getId(), menteeId, MentorshipStatus.PENDING)) {
            throw new DuplicateResourceException("You already have a pending request with this mentor");
        }

        User mentee = userRepo.findById(menteeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", menteeId));

        MentorshipRequest req = MentorshipRequest.builder()
                .mentor(mentor)
                .mentee(mentee)
                .message(request.getMessage())
                .status(MentorshipStatus.PENDING)
                .build();

        requestRepo.save(req);
        log.info("Mentorship request created: mentor={} mentee={}", mentor.getId(), menteeId);
    }

    @Transactional
    public void respondToRequest(Long requestId, String action, Long mentorUserId) {
        MentorshipRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship request", requestId));

        if (!req.getMentor().getId().equals(mentorUserId)) {
            throw new ForbiddenException("Only the mentor can respond to this request");
        }

        if (req.getStatus() != MentorshipStatus.PENDING) {
            throw new BadRequestException("Request has already been responded to");
        }

        req.setStatus("ACCEPT".equalsIgnoreCase(action)
                ? MentorshipStatus.ACCEPTED : MentorshipStatus.REJECTED);
        req.setRespondedAt(Instant.now());
        requestRepo.save(req);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private MentorResponse toResponse(MentorProfile mp) {
        User user = mp.getUser();
        List<String> skills = skillRepo.findByUserId(user.getId()).stream()
                .map(s -> s.getSkill())
                .toList();

        String year = null;
        String branch = null;
        StudentProfile sp = studentProfileRepo.findByUserId(user.getId()).orElse(null);
        if (sp != null) {
            year = sp.getYear();
            branch = sp.getBranch();
        }

        return MentorResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .year(year)
                .branch(branch)
                .headline(mp.getHeadline())
                .skills(skills)
                .avgRating(mp.getAvgRating())
                .totalSessions(mp.getTotalSessions())
                .responseTime(mp.getResponseTime())
                .available(mp.isAvailable())
                .emoji("🎓")
                .build();
    }
}
