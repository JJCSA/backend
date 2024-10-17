package com.jjcsa.mapper;

import com.jjcsa.dto.events.CreateEventDto;
import com.jjcsa.model.events.Event;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface EventMapper {

    Event toEvents(CreateEventDto createEventDto);
}
