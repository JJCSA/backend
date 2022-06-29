package com.jjcsa.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jjcsa.model.Education;
import com.jjcsa.model.WorkEx;
import com.jjcsa.model.enumModel.ContactMethod;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.model.enumModel.VolunteeringInterest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private List<Education> educationList;
    private List<WorkEx> workExperience;
    private Date lastUpdatedDate;
    private Date approvedDate;
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
    private String socialMediaPlatform;
    private VolunteeringInterest volunteeringInterest;
    private boolean loanTaken;
    private String loanOrganization;
    private String linkedinUrl;
    private String description;
    private boolean contactShared;
    private String country;
    private boolean userStudent;
    private UserRole userRole;
    private String profilePictureURL;
    private String communityDocURL;
}
