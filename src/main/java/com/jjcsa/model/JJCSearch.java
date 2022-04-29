package com.jjcsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "JJC_SEARCH_V")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JJCSearch implements Serializable {

    @Id
    @Column(name = "USER_ID")
    @JsonIgnore
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

    // This will be false as boolean does not support null values. Null will be treated as false
    @Transient
    private boolean isRegionalContact;
}
