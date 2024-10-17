package com.jjcsa.service;

import com.jjcsa.dto.events.CreateEventDto;
import com.jjcsa.mapper.EventMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.events.Event;
import com.jjcsa.repository.events.EventsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventsRepository eventsRepository;
    private final EventMapper eventMapper;

    public Event createEvent(CreateEventDto event, User adminUser) {

        // Validations
        // endDate should be after startDate
        DateTime startTime = event.getStartTime();
        DateTime endTime = event.getEndTime();
        if (nonNull(startTime) && nonNull(endTime)
            && startTime.isAfter(endTime)) {
            // throw error
            return null;
        }

        // registrationDeadline should be after now
        DateTime registrationDeadline = event.getRegistrationDeadline();
        if (nonNull(registrationDeadline) && registrationDeadline.isBeforeNow()) {
            // throw error
            return null;
        }

        Event newEvent = eventMapper.toEvents(event);
        newEvent.setCreatedByUserId(adminUser.getId());

        return eventsRepository.save(newEvent);
    }
}
