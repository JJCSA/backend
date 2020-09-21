package com.jjcsa.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "education")
public @Data class Education{

    @Id
    @Column(name="educ_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int educationId;

    @Column(name="university_name")
    private String universityName;

    @Column(name="grad_month")
    private int gradMonth;

    @Column(name="grad_year")
    private int gradYear;
    private String specialization;
    private String degree;

}