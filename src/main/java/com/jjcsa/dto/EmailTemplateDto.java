package com.jjcsa.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EmailTemplateDto {
    private String body;
    private String subject;
}
