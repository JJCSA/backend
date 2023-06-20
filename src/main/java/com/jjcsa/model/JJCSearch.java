package com.jjcsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "V_JJC_SEARCH")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JJCSearch implements Serializable {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATE")
    private String state;

    @Column(name = "CITY")
    private String city;

    @Column(name = "SPECIALIZATION")
    private String specialization;

    @Column(name = "UNIVERSITY_NAME")
    private String universityName;

    @Column(name = "WORK_ROLE")
    private String workRole;

    @Column(name = "LINKEDIN_URL")
    private String linkedinUrl;

    @Column(name = "IS_REGIONAL_CONTACT")
    private boolean isRegionalContact;

    @Column(name = "ABOUT_ME")
    private String aboutMe;

    @Column(name = "profile_picture_url")
    private String profilePicture;
}
