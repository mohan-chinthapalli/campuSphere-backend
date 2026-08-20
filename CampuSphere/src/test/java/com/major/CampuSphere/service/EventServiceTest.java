package com.major.CampuSphere.service;

import com.major.CampuSphere.entity.Event;
import com.major.CampuSphere.entity.EventRegistration;
import com.major.CampuSphere.entity.User;
import com.major.CampuSphere.enums.Role;
import com.major.CampuSphere.exception.BadRequestException;
import com.major.CampuSphere.exception.DuplicateResourceException;
import com.major.CampuSphere.exception.ResourceNotFoundException;
import com.major.CampuSphere.repository.EventDiscussionRepository;
import com.major.CampuSphere.repository.EventRegistrationRepository;
import com.major.CampuSphere.repository.EventRepository;
import com.major.CampuSphere.repository.UserRepository;
import com.major.CampuSphere.service.impl.EventServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock EventRepository eventRepo;
    @Mock EventRegistrationRepository registrationRepo;
    @Mock EventDiscussionRepository discussionRepo;
    @Mock UserRepository userRepo;

    @InjectMocks EventServiceImpl eventService;

    private User testUser() {
        return User.builder().id(2L).name("Test Student")
                .email("s@test.edu").role(Role.STUDENT).active(true).build();
    }

    private Event testEvent(int seats) {
        return Event.builder()
                .id(1L).slug("test-event").title("Test Event")
                .category("Hackathon").seatsTotal(seats).build();
    }

    @Test
    void getBySlug_notFound_throwsResourceNotFoundException() {
        when(eventRepo.findBySlug("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getBySlug("non-existent", null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void register_alreadyRegistered_throwsDuplicateResourceException() {
        Event event = testEvent(100);
        when(eventRepo.findBySlug("test-event")).thenReturn(Optional.of(event));
        when(registrationRepo.existsByEventIdAndUserId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> eventService.register("test-event", 2L))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void register_eventFull_throwsBadRequestException() {
        Event event = testEvent(10);
        when(eventRepo.findBySlug("test-event")).thenReturn(Optional.of(event));
        when(registrationRepo.existsByEventIdAndUserId(1L, 2L)).thenReturn(false);
        when(registrationRepo.countByEventId(1L)).thenReturn(10L); // seats full

        assertThatThrownBy(() -> eventService.register("test-event", 2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("fully booked");
    }

    @Test
    void register_success_savesRegistration() {
        Event event = testEvent(100);
        User user = testUser();
        when(eventRepo.findBySlug("test-event")).thenReturn(Optional.of(event));
        when(registrationRepo.existsByEventIdAndUserId(1L, 2L)).thenReturn(false);
        when(registrationRepo.countByEventId(1L)).thenReturn(5L);
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(eventRepo.findBySlug("test-event")).thenReturn(Optional.of(event));
        when(registrationRepo.countByEventId(1L)).thenReturn(6L);

        // Should not throw
        eventService.register("test-event", 2L);
        verify(registrationRepo).save(any(EventRegistration.class));
    }

    @Test
    void unregister_noRegistration_throwsResourceNotFoundException() {
        Event event = testEvent(100);
        when(eventRepo.findBySlug("test-event")).thenReturn(Optional.of(event));
        when(registrationRepo.findByEventIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.unregister("test-event", 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
