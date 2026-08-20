package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.CreateAnnouncementRequest;
import com.major.CampuSphere.dto.response.AnnouncementResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.entity.Announcement;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.AnnouncementTag;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.AnnouncementRepository;
import com.major.CampuSphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl {

    private final AnnouncementRepository announcementRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public PageResponse<AnnouncementResponse> list(String tag, String q, Pageable pageable) {
        AnnouncementTag tagEnum = null;
        if (tag != null && !tag.isBlank()) {
            try {
                tagEnum = AnnouncementTag.valueOf(tag.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // invalid tag → treat as no filter
            }
        }
        Page<Announcement> page = announcementRepo.search(tagEnum,
                (q != null && q.isBlank()) ? null : q, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse getById(Long id) {
        return toResponse(announcementRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id)));
    }

    @Transactional
    public AnnouncementResponse create(CreateAnnouncementRequest request, Long creatorId) {
        User creator = userRepo.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", creatorId));

        Announcement a = Announcement.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .author(request.getAuthor())
                .tag(request.getTag())
                .priority(request.getPriority() != null ? request.getPriority()
                        : com.major.CampuSphere.enums.Priority.NORMAL)
                .createdBy(creator)
                .build();

        a = announcementRepo.save(a);
        log.info("Announcement created: id={} by userId={}", a.getId(), creatorId);
        return toResponse(a);
    }

    @Transactional
    public void delete(Long id) {
        Announcement a = announcementRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", id));
        announcementRepo.delete(a);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private AnnouncementResponse toResponse(Announcement a) {
        return AnnouncementResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .body(a.getBody())
                .author(a.getAuthor())
                .tag(a.getTag())
                .priority(a.getPriority())
                .publishedAt(a.getPublishedAt())
                .build();
    }
}
