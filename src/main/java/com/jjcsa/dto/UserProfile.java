package com.jjcsa.dto;

import com.jjcsa.model.Education;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.ContactMethod;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.model.enumModel.VolunteeringInterest;
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
    private UUID id;
    @NotNull
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private ContactMethod contactMethod;
    private String communityName;
    private UserStatus userStatus;
    private String street;
    private String city;
    private String state;
    private String zip;
    private Date dateOfBirth;
    private String profilePicture;
    private String socialMediaPlatform;
    private VolunteeringInterest volunteeringInterest;
    private String linkedinUrl;

    private List<Education> education;
    private List<WorkEx> workExperience;
}
