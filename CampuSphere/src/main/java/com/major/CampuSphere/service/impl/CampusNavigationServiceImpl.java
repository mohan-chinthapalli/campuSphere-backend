package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.response.CampusPlaceResponse;
import com.major.CampuSphere.entity.CampusPlace;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.CampusPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampusNavigationServiceImpl {

    private final CampusPlaceRepository placeRepo;

    @Transactional(readOnly = true)
    public List<CampusPlaceResponse> listPlaces(String q) {
        return placeRepo.search((q != null && q.isBlank()) ? null : q)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CampusPlaceResponse getBySlug(String slug) {
        CampusPlace place = placeRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Campus place", slug));
        return toResponse(place);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private CampusPlaceResponse toResponse(CampusPlace p) {
        return CampusPlaceResponse.builder()
                .id(p.getId())
                .slug(p.getSlug())
                .name(p.getName())
                .type(p.getType())
                .floors(p.getFloors())
                .openHours(p.getOpenHours())
                .mapX(p.getMapX())
                .mapY(p.getMapY())
                .walkTime(p.getWalkTime())
                .build();
    }
}
