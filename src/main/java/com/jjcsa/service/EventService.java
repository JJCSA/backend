package com.jjcsa.service;

import com.jjcsa.dto.events.CreateEventDto;
import com.jjcsa.mapper.EventMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.events.Event;
import com.jjcsa.repository.events.EventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventsRepository eventsRepository;
    private final EventMapper eventMapper;

    public Event createEvent(CreateEventDto event, User adminUser) {

        Event newEvent = eventMapper.toEvents(event);
        // Validations
        // endDate should be after startDate
        LocalDateTime startTime = newEvent.getStartTime();
        LocalDateTime endTime = newEvent.getEndTime();
        if (nonNull(startTime) && nonNull(endTime)
            && startTime.isAfter(endTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time is after end time");
        }

        // registrationDeadline should be after now
        LocalDateTime registrationDeadline = newEvent.getRegistrationDeadline();
        if (nonNull(registrationDeadline) && registrationDeadline.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration deadline has to be in future");
        }

        // assignments
        newEvent.setCreatedByUserId(adminUser.getId());

        return eventsRepository.save(newEvent);
    }
}
