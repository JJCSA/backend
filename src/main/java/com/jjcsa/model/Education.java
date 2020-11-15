package com.jjcsa.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "education")
@NoArgsConstructor
public @Data class Education{

    @Id
    @Column(name="educ_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int educationId;

    @Column(name="university_name", columnDefinition = "varchar(45) default ''")
    private String universityName;

    @Column(name="grad_month", columnDefinition = "integer default 11")
    private int gradMonth;

    @Column(name="grad_year", columnDefinition = "integer default 1111")
    private int gradYear;

    @Column(columnDefinition = "varchar(45) default ''")
    private String specialization;

    @Column(columnDefinition = "varchar(100) default ''")
    private String degree;

}