package com.jjcsa.dto;

import com.jjcsa.model.Education;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String id;
    @NotNull
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private ContactMethod contactMethod;
    private String communityName;
    private UserStatus userStatus;
    private UserRole userRole;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;
    private Date dateOfBirth;
    private String profilePicture;
    private String socialMediaPlatform;
    private VolunteeringInterest volunteeringInterest;
    private String linkedinUrl;
    private Boolean userStudent;
    private Gender gender;
    private String aboutMe;

    private List<Education> education;
    private List<WorkEx> workExperience;
}
