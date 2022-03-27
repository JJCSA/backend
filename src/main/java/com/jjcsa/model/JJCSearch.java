package com.jjcsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "JJC_SEARCH_VIEW")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JJCSearch implements Serializable {

    @Id
    @Column(name = "USER_ID")
    @JsonIgnore
    private String userId;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "LIVING_STATE")
    private String livingState;

    @Column(name = "LIVING_CITY")
    private String livingCity;

    @Column(name = "WORK_SPECIALIZATION")
    private String workSpecialization;

    @Column(name = "UNIVERSITY")
    private String university;

    @Column(name = "WORK_ROLE")
    private String workRole;

    // This will be false as boolean does not support null values. Null will be treated as false
    @Transient
    private boolean isRegionalContact;
}