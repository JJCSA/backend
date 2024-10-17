package com.jjcsa.dto.events;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class CreateEventDto {

    @NotBlank
    private String title;

    private String shortDescription;
    private String longDescription;
    private String location;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //ISO Date Time Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    private DateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //ISO Date Time Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    private DateTime endTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) //ISO Date Time Format yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    private DateTime registrationDeadline;

    private String meetingLink;

//    private Set<EventResourceDto> eventResources;
//    private Set<SpeakerDto> speakers;

}
