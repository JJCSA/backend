package com.jjcsa.dto.events;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CreateEventDto {

    @NotBlank(message = "Event Title cannot be empty")
    private String title;

    private String shortDescription;
    private String longDescription;
    private String location;

    //ISO Date Time Format yyyy-MM-dd'T'HH:mm:ss
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "startTime must be of the format yyyy-MM-dd'T'HH:mm:ss")
    private String startTime;

    //ISO Date Time Format yyyy-MM-dd'T'HH:mm:ss
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "endTime must be of the format yyyy-MM-dd'T'HH:mm:ss")
    private String endTime;

    //ISO Date Time Format yyyy-MM-dd'T'HH:mm:ss
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", message = "registrationDeadline must be of the format yyyy-MM-dd'T'HH:mm:ss")
    private String registrationDeadline;

    private String meetingLink;

//    private Set<EventResourceDto> eventResources;
//    private Set<SpeakerDto> speakers;

}
