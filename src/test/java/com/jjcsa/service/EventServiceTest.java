package com.jjcsa.service;

import com.jjcsa.dto.events.CreateEventDto;
import com.jjcsa.mapper.EventMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.events.Event;
import com.jjcsa.repository.events.EventsRepository;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventsRepository eventsRepository;
    @Mock private EventMapper eventMapper;

    @Spy @InjectMocks private EventService eventService;

    @Test
    void shouldCreateEvent() {
        Event event = new Event();
        event.setId("1");
        event.setTitle("Event Title");
        event.setShortDescription("Event ShortDescription");
        event.setLongDescription("Event Long Description");
        event.setLocation("Zoom");
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        event.setStartTime(now.plusDays(2));
        event.setEndTime(event.getStartTime().plusHours(2));
        event.setIsPublished(false);
        event.setRegistrationDeadline(now.plusDays(1));
        event.setMeetingLink("zoom.com/123");

        User adminUser = User.builder()
                .id(UUID.randomUUID().toString())
                .email("admin@admin.com")
                .build();

        CreateEventDto eventDto = new CreateEventDto();
        eventDto.setTitle("Event Title");
        eventDto.setShortDescription("Event ShortDescription");
        eventDto.setLongDescription("Event Long Description");
        eventDto.setLocation("Zoom");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        eventDto.setStartTime(now.plusDays(2).format(formatter));
        eventDto.setEndTime(event.getStartTime().plusHours(2).format(formatter));
        eventDto.setRegistrationDeadline(now.plusDays(1).format(formatter));
        eventDto.setMeetingLink("zoom.com/123");

        when(eventMapper.toEvents(any())).thenReturn(event);
        when(eventsRepository.save(any())).thenReturn(event);

        Event savedEvent = eventService.createEvent(eventDto, adminUser);
        assertEquals(eventDto.getTitle(), savedEvent.getTitle());
        assertEquals(eventDto.getShortDescription(), savedEvent.getShortDescription());
        assertEquals(eventDto.getLongDescription(), savedEvent.getLongDescription());
        assertEquals(eventDto.getLocation(), savedEvent.getLocation());
        assertEquals(eventDto.getStartTime(), savedEvent.getStartTime().format(formatter));
        assertEquals(eventDto.getEndTime(), savedEvent.getEndTime().format(formatter));
        assertEquals(eventDto.getRegistrationDeadline(), savedEvent.getRegistrationDeadline().format(formatter));
        assertEquals(eventDto.getMeetingLink(), savedEvent.getMeetingLink());
        assertNull(savedEvent.getStatus());
    }
}