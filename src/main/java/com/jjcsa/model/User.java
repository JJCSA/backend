package com.jjcsa.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jjcsa.model.enumModel.*;
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

    // *********************************************** //
    // Constructors
    // *********************************************** //

    public User(String email) {
        this.email = email;
    }

    // *********************************************** //
    // Properties
    // *********************************************** //

    @Id
    private String id;
    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Column(name = "last_updated_date", columnDefinition = "varchar(45) default '11/11/1111'")
    private Date lastUpdatedDate;

    @PrePersist
    protected void onUpdate() {
        lastUpdatedDate = new Date();
    }

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

    @Column(name = "community_name", columnDefinition = "varchar(100) default ''")
    private String communityName;

    @Column(name = "community_document_url", columnDefinition = "varchar(100) default ''")
    private String communityDocumentURL;

    @Column(columnDefinition = "varchar(45) default ''")
    private String street;

    @Column(columnDefinition = "varchar(45) default ''")
    private String city;

    @Column(columnDefinition = "varchar(45) default ''")
    private String state;

    @Pattern(regexp = "[\\s]*[0-9]*[1-9]+",message="Enter digits only")
    @Column(columnDefinition = "varchar(10) default ''")
    private String zip;

    @Column(name = "dob")
    private String dateOfBirth;

    @Column(name = "profile_picture_url", columnDefinition = "varchar(100) default ''")
    private String profilePicture;

    @Column(name = "socialmedia_platform", columnDefinition = "varchar(45) default ''")
    private String socialMediaPlatform;

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

    @Column(name = "country")
    private String country;

    @Column(name = "is_user_student")
    private Boolean userStudent;

    @Column(name = "about_me")
    private String aboutMe;

    @Transient
    private UserRole userRole;

    @Column(name = "is_regional_contact", columnDefinition = "boolean default false")
    private Boolean isRegionalContact;

    // *********************************************** //
    // Enumerations
    // *********************************************** //

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_method", columnDefinition = "varchar(45) default 'Whatsapp'")
    private ContactMethod contactMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", columnDefinition = "varchar(45) default 'Pending'")
    private UserStatus userStatus;

    @Column(name = "volunteering_interest")
    private String volunteeringInterest;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    // *********************************************** //
    // Relations
    // *********************************************** //

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Education> educationList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WorkEx> workExperience;

}   