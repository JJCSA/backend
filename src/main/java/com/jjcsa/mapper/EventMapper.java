package com.jjcsa.mapper;

import com.jjcsa.dto.events.CreateEventDto;
import com.jjcsa.model.events.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mappings({
            @Mapping(target = "startTime", dateFormat = "yyyy-MM-dd'T'HH:mm:ss"),
            @Mapping(target = "endTime", dateFormat = "yyyy-MM-dd'T'HH:mm:ss"),
            @Mapping(target = "registrationDeadline", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    })
    Event toEvents(CreateEventDto createEventDto);
}
