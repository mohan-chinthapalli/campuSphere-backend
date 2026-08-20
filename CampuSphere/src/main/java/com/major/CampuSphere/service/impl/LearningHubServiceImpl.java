package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.UpdateProgressRequest;
import com.major.CampuSphere.dto.response.MaterialResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.dto.response.SubjectResponse;
import com.major.CampuSphere.entity.LearningMaterial;
import com.major.CampuSphere.entity.MaterialProgress;
import com.major.CampuSphere.entity.Subject;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.MaterialType;
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
public class LearningHubServiceImpl {

    private final LearningMaterialRepository materialRepo;
    private final MaterialProgressRepository progressRepo;
    private final SubjectRepository subjectRepo;
    private final UserRepository userRepo;

    // ─── Subjects ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<SubjectResponse> listSubjects(Integer semester) {
        List<Subject> subjects = semester != null
                ? subjectRepo.findBySemesterOrderByCode(semester)
                : subjectRepo.findAllByOrderBySemesterAscCodeAsc();
        return subjects.stream().map(this::toSubjectResponse).toList();
    }

    // ─── Materials ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<MaterialResponse> listMaterials(Integer semester, String subjectCode,
                                                         String type, String q,
                                                         Long currentUserId, Pageable pageable) {
        MaterialType typeEnum = null;
        if (type != null && !type.isBlank()) {
            try {
                typeEnum = MaterialType.valueOf(type.toUpperCase().replace(" ", "_"));
            } catch (IllegalArgumentException ignored) {}
        }

        Page<LearningMaterial> page = materialRepo.search(
                semester,
                (subjectCode != null && subjectCode.isBlank()) ? null : subjectCode,
                typeEnum,
                (q != null && q.isBlank()) ? null : q,
                pageable);

        return PageResponse.from(page.map(m -> toMaterialResponse(m, currentUserId)));
    }

    @Transactional(readOnly = true)
    public MaterialResponse getByKey(String materialKey, Long currentUserId) {
        LearningMaterial material = materialRepo.findByMaterialKey(materialKey)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialKey));
        return toMaterialResponse(material, currentUserId);
    }

    @Transactional(readOnly = true)
    public MaterialResponse getById(Long id, Long currentUserId) {
        LearningMaterial material = materialRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material", id));
        return toMaterialResponse(material, currentUserId);
    }

    // ─── Bookmarked materials ─────────────────────────────────────

    @Transactional(readOnly = true)
    public List<MaterialResponse> getBookmarked(Long userId) {
        return progressRepo.findByUserIdAndBookmarkedTrue(userId).stream()
                .map(p -> toMaterialResponse(p.getMaterial(), userId))
                .toList();
    }

    // ─── Progress & Bookmarks ─────────────────────────────────────

    @Transactional
    public MaterialResponse updateProgress(Long materialId, UpdateProgressRequest request, Long userId) {
        LearningMaterial material = materialRepo.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        MaterialProgress progress = progressRepo
                .findByMaterialIdAndUserId(materialId, userId)
                .orElseGet(() -> MaterialProgress.builder()
                        .material(material)
                        .user(user)
                        .build());

        progress.setProgressPct(request.getProgressPct());
        if (request.getBookmarked() != null) {
            progress.setBookmarked(request.getBookmarked());
        }
        progress.setLastReadAt(Instant.now());
        progressRepo.save(progress);

        return toMaterialResponse(material, userId);
    }

    @Transactional
    public MaterialResponse toggleBookmark(Long materialId, Long userId) {
        LearningMaterial material = materialRepo.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        MaterialProgress progress = progressRepo
                .findByMaterialIdAndUserId(materialId, userId)
                .orElseGet(() -> MaterialProgress.builder()
                        .material(material)
                        .user(user)
                        .build());

        progress.setBookmarked(!progress.isBookmarked());
        progressRepo.save(progress);

        return toMaterialResponse(material, userId);
    }

    @Transactional
    public MaterialResponse recordDownload(Long materialId, Long userId) {
        LearningMaterial material = materialRepo.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material", materialId));
        material.setDownloadCount(material.getDownloadCount() + 1);
        materialRepo.save(material);
        log.info("Download recorded: materialId={} userId={}", materialId, userId);
        return toMaterialResponse(material, userId);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private MaterialResponse toMaterialResponse(LearningMaterial m, Long currentUserId) {
        Integer progressPct = null;
        boolean bookmarked = false;
        Instant lastReadAt = null;

        if (currentUserId != null) {
            var progressOpt = progressRepo.findByMaterialIdAndUserId(m.getId(), currentUserId);
            if (progressOpt.isPresent()) {
                MaterialProgress p = progressOpt.get();
                progressPct = p.getProgressPct();
                bookmarked = p.isBookmarked();
                lastReadAt = p.getLastReadAt();
            }
        }

        return MaterialResponse.builder()
                .id(m.getId())
                .materialKey(m.getMaterialKey())
                .title(m.getTitle())
                .materialType(m.getMaterialType())
                .subjectCode(m.getSubjectCode())
                .semester(m.getSemester())
                .author(m.getAuthor())
                .fileSize(m.getFileSize())
                .downloadCount(m.getDownloadCount())
                .emoji(m.getEmoji())
                .gradient(m.getGradient())
                .progressPct(progressPct)
                .bookmarked(bookmarked)
                .lastReadAt(lastReadAt)
                .createdAt(m.getCreatedAt())
                .build();
    }

    private SubjectResponse toSubjectResponse(Subject s) {
        return SubjectResponse.builder()
                .id(s.getId())
                .code(s.getCode())
                .name(s.getName())
                .emoji(s.getEmoji())
                .semester(s.getSemester())
                .facultyName(s.getFaculty() != null ? s.getFaculty().getName() : null)
                .build();
    }
}
