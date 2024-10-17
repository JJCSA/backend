package com.jjcsa.dto.events;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SpeakerDto {
    @NotBlank
    private String speakerName;
    private String speakerDescription;
}
