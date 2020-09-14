package com.jjcsa.model;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.DateTimeFormat;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.pattern;

import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "user_profile")
public class UserProfile{
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    @Column(name = "last_updated_date")
    private Date lastUpdatedDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
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

    @DateTimeFormat(pattern = "dd/MM/yyyy")
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

	public UUID getId() {
		return this.id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public Date getLastUpdatedDate() {
		return this.lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public Date getApprovedDate() {
		return this.approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNumber() {
		return this.mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public ContactMethod getContactMethod() {
		return this.contactMethod;
	}

	public void setContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
	}

	public String getCommunityName() {
		return this.communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public MultipartFile getCommunityDocument() {
		return this.communityDocument;
	}

	public void setCommunityDocument(MultipartFile communityDocument) {
		this.communityDocument = communityDocument;
	}

	public UserStatus getUserStatus() {
		return this.userStatus;
	}

	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public MultipartFile getProfilePicture() {
		return this.profilePicture;
	}

	public void setProfilePicture(MultipartFile profilePicture) {
		this.profilePicture = profilePicture;
	}

	public char getSocialMediaPlatform() {
		return this.socialMediaPlatform;
	}

	public void setSocialMediaPlatform(char socialMediaPlatform) {
		this.socialMediaPlatform = socialMediaPlatform;
	}

	public VolunteeringInterest getVolInterest() {
		return this.volInterest;
	}

	public void setVolInterest(VolunteeringInterest volInterest) {
		this.volInterest = volInterest;
	}

	public boolean isLoanTaken() {
		return this.loanTaken;
	}

	public void setLoanTaken(boolean loanTaken) {
		this.loanTaken = loanTaken;
	}

	public String getLoanOrganization() {
		return this.loanOrganization;
	}

	public void setLoanOrganization(String loanOrganization) {
		this.loanOrganization = loanOrganization;
	}

	public String getLinkedinUrl() {
		return this.linkedinUrl;
	}

	public void setLinkedinUrl(String linkedinUrl) {
		this.linkedinUrl = linkedinUrl;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isContactShared() {
		return this.contactShared;
	}

	public void setContactShared(boolean contactShared) {
		this.contactShared = contactShared;
	}

   
    //@DateTimeFormat(pattern = "dd/MM/yyyy") 
}   