package com.jjcsa.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jjcsa.model.enumModel.ContactMethod;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.model.enumModel.VolunteeringInterest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "user_account") // user is a reserved keyword in postgres
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String email;

    public User(String email) {
        this.email = email;
    }

    @OneToMany(mappedBy = "user")
    private List<Education> educationList;

    @OneToMany(mappedBy = "user")
    private List<WorkEx> workExperience;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "last_updated_date", columnDefinition = "varchar(45) default '11/11/1111'")
    private Date lastUpdatedDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "date_approved", columnDefinition = "varchar(45) default '11/11/1111'")
    private Date approvedDate;
    
    @Column(name = "first_name", columnDefinition = "varchar(45) default ''")
    private String firstName;

    @Column(name = "middle_name", columnDefinition = "varchar(45) default ''")
    private String middleName;

    @Column(name = "last_name", columnDefinition = "varchar(45) default ''")
    private String lastName;

    @Column(name = "mobile", columnDefinition = "varchar(20) default '9999999999'")
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_method", columnDefinition = "varchar(45) default 'Whatsapp'")
    private ContactMethod contactMethod;

    @Column(name = "community_name", columnDefinition = "varchar(100) default ''")
    private String communityName;

    @Column(name = "community_document_url", columnDefinition = "varchar(100) default ''")
    private String communityDocumentURL;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", columnDefinition = "varchar(45) default 'Pending'")
    private UserStatus userStatus;

    @Column(columnDefinition = "varchar(45) default ''")
    private String street;

    @Column(columnDefinition = "varchar(45) default ''")
    private String city;

    @Column(columnDefinition = "varchar(45) default ''")
    private String state;

    @Pattern(regexp = "[\\s]*[0-9]*[1-9]+",message="Enter digits only")
    @Column(columnDefinition = "varchar(10) default ''")
    private String zip;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "dob", columnDefinition = "varchar(45) default '11/11/1111'")
    private Date dateOfBirth;

    @Column(name = "profile_picture_url", columnDefinition = "varchar(100) default ''")
    private String profilePicture;

    @Column(name = "socialmedia_platform", columnDefinition = "varchar(45) default ''")
    private String socialMediaPlatform;

    @Enumerated(EnumType.STRING)
    @Column(name = "volunteering_interest", columnDefinition = "varchar(45) default 'No'")
    private VolunteeringInterest volunteeringInterest;

    @Column(name = "loan_taken", columnDefinition = "boolean default false")
    private boolean loanTaken;

    @Column(name = "loan_organization", columnDefinition = "varchar(45) default ''")
    private String loanOrganization;

    @Column(name = "linkedin_url", columnDefinition = "varchar(100) default ''")
    private String linkedinUrl;

    @Column(columnDefinition = "varchar(256) default ''")
    private String description;
    
    @Column(name = "contact_share", columnDefinition = "boolean default false")
    private boolean contactShared;
}   