package com.jjcsa.model;

import java.util.Date;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjcsa.model.enumModel.ContactMethod;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.model.enumModel.VolunteeringInterest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "user_profile")
public @Data class UserProfile{
    @Id
    @Column(name="user_id")
    private UUID id;

    @OneToOne
    @MapsId
    private UserLogin userLogin;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "last_updated_date")
    private Date lastUpdatedDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "date_approved")
    private Date approvedDate;
    
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile")
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_method")
    private ContactMethod contactMethod;

    @Column(name = "community_name")
    private String communityName;

    @Transient
    @Column(name = "community_document")
    private MultipartFile communityDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;

    private String street;
    private String city;
    private String state;

    @Pattern(regexp = "[\\s]*[0-9]*[1-9]+",message="Enter digits only")
    private String zip;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "dob")
    private Date dateOfBirth;

    @Transient
    @Column(name = "profile_picture")
    private MultipartFile profilePicture;

    @Column(name = "socialmedia_platform")
    private char socialMediaPlatform;

    @Enumerated(EnumType.STRING)
    @Column(name = "volunteering_interest")
    private VolunteeringInterest volInterest;

    @Column(name = "loan_taken")
    private boolean loanTaken;

    @Column(name = "loan_organization")
    private String loanOrganization;

    @Column(name = "linkedin_url")
    private String linkedinUrl;
    private String description;
    
    @Column(name = "contact_share")
    private boolean contactShared;
}   