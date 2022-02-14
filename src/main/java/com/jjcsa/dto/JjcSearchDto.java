package com.jjcsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class JjcSearchDto {
    private String name;
    private String universityName;
    private String specialization;
    private String workRole;
    private String state;
    private String city;
  //  private boolean isRegionalContact;

}
