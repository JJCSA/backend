package com.jjcsa.dto.events;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EventResourceDto {
    @NotBlank
    private String resourceType; // poster, material, video, etc.
    @NotBlank
    private String resourceId; // event_resources.id --> should be used after uploading resource
}
