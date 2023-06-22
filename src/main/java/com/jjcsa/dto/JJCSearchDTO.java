package com.jjcsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JJCSearchDTO {

    private String userId;
    private String name;
    private String state;
    private String city;
    private String specialization;
    private String universityName;
    private String workRole;
    private String linkedinUrl;
    private Boolean isRegionalContact;
    private String aboutMe;
    private String profilePicture;

}
