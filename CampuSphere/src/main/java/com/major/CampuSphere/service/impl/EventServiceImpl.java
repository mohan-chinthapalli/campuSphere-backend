package com.major.CampuSphere.service.impl;

import com.major.CampuSphere.dto.request.CreateEventRequest;
import com.major.CampuSphere.dto.response.EventResponse;
import com.major.CampuSphere.dto.response.PageResponse;
import com.major.CampuSphere.entity.*;
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

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl {

    private final EventRepository eventRepo;
    private final EventRegistrationRepository registrationRepo;
    private final EventDiscussionRepository discussionRepo;
    private final UserRepository userRepo;

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> listEvents(String category, String q, Long currentUserId, Pageable pageable) {
        Page<Event> page = eventRepo.searchEvents(
                category != null && category.isBlank() ? null : category,
                q != null && q.isBlank() ? null : q,
                pageable);
        return PageResponse.from(page.map(e -> toResponse(e, currentUserId)));
    }

    @Transactional(readOnly = true)
    public EventResponse getBySlug(String slug, Long currentUserId) {
        Event event = eventRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", slug));
        return toResponse(event, currentUserId);
    }

    @Transactional
    public EventResponse create(CreateEventRequest request, Long creatorId) {
        User creator = userRepo.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", creatorId));

        String slug = toSlug(request.getTitle());
        if (eventRepo.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Event event = Event.builder()
                .slug(slug)
                .title(request.getTitle())
                .category(request.getCategory())
                .tagline(request.getTagline())
                .description(request.getDescription())
                .prize(request.getPrize())
                .eventDate(request.getEventDate())
                .startsAt(request.getStartsAt())
                .displayTime(request.getDisplayTime())
                .venue(request.getVenue())
                .seatsTotal(request.getSeatsTotal())
                .organizerName(request.getOrganizerName())
                .organizerClub(request.getOrganizerClub())
                .organizerEmail(request.getOrganizerEmail())
                .gradient(request.getGradient())
                .emoji(request.getEmoji())
                .tags(request.getTags() != null ? String.join(",", request.getTags()) : null)
                .createdBy(creator)
                .build();

        if (request.getAgenda() != null) {
            int order = 0;
            for (CreateEventRequest.AgendaItemRequest item : request.getAgenda()) {
                EventAgenda agenda = EventAgenda.builder()
                        .event(event)
                        .time(item.getTime())
                        .title(item.getTitle())
                        .sortOrder(order++)
                        .build();
                event.getAgenda().add(agenda);
            }
        }

        event = eventRepo.save(event);
        log.info("Event created: slug={} by userId={}", event.getSlug(), creatorId);
        return toResponse(event, creatorId);
    }

    @Transactional
    public EventResponse register(String slug, Long userId) {
        Event event = eventRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", slug));

        if (registrationRepo.existsByEventIdAndUserId(event.getId(), userId)) {
            throw new DuplicateResourceException("You are already registered for this event");
        }

        long currentCount = registrationRepo.countByEventId(event.getId());
        if (event.getSeatsTotal() > 0 && currentCount >= event.getSeatsTotal()) {
            throw new BadRequestException("Event is fully booked");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        EventRegistration reg = EventRegistration.builder()
                .event(event)
                .user(user)
                .build();
        registrationRepo.save(reg);

        log.info("User {} registered for event {}", userId, slug);
        return getBySlug(slug, userId);
    }

    @Transactional
    public void unregister(String slug, Long userId) {
        Event event = eventRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", slug));

        EventRegistration reg = registrationRepo.findByEventIdAndUserId(event.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        registrationRepo.delete(reg);
        log.info("User {} unregistered from event {}", userId, slug);
    }

    @Transactional
    public void delete(String slug, Long userId) {
        Event event = eventRepo.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event", slug));

        if (event.getCreatedBy() != null && !event.getCreatedBy().getId().equals(userId)) {
            throw new ForbiddenException("Only the event creator can delete it");
        }
        eventRepo.delete(event);
        log.info("Event deleted: slug={}", slug);
    }

    // ─── Mapping ──────────────────────────────────────────────────

    private EventResponse toResponse(Event e, Long currentUserId) {
        boolean registered = currentUserId != null &&
                registrationRepo.existsByEventIdAndUserId(e.getId(), currentUserId);

        long regCount = registrationRepo.countByEventId(e.getId());

        List<EventResponse.AgendaItem> agendaItems = e.getAgenda().stream()
                .map(a -> EventResponse.AgendaItem.builder()
                        .time(a.getTime())
                        .title(a.getTitle())
                        .build())
                .toList();

        List<String> tags = (e.getTags() != null && !e.getTags().isBlank())
                ? Arrays.asList(e.getTags().split(","))
                : List.of();

        return EventResponse.builder()
                .id(e.getId())
                .slug(e.getSlug())
                .title(e.getTitle())
                .category(e.getCategory())
                .tagline(e.getTagline())
                .description(e.getDescription())
                .prize(e.getPrize())
                .eventDate(e.getEventDate())
                .startsAt(e.getStartsAt())
                .displayTime(e.getDisplayTime())
                .venue(e.getVenue())
                .seatsTotal(e.getSeatsTotal())
                .seatsLeft(Math.max(0, e.getSeatsTotal() - (int) regCount))
                .participants((int) regCount)
                .gradient(e.getGradient())
                .emoji(e.getEmoji())
                .tags(tags)
                .organizer(EventResponse.OrganizerInfo.builder()
                        .name(e.getOrganizerName())
                        .club(e.getOrganizerClub())
                        .email(e.getOrganizerEmail())
                        .build())
                .agenda(agendaItems)
                .registeredByCurrentUser(registered)
                .createdAt(e.getCreatedAt())
                .build();
    }

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return NON_LATIN.matcher(
                WHITESPACE.matcher(normalized.toLowerCase(Locale.ENGLISH)).replaceAll("-")
        ).replaceAll("").replaceAll("-+", "-").replaceAll("^-|-$", "");
    }
}
