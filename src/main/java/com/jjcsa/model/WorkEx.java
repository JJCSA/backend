package com.jjcsa.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "work_ex")
public @Data class WorkEx {

    @Id
    @Column(name = "exp_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int expId;

    @Column(name = "company_name")
    private String companyName;

    private String role;
    private String location;

    @Column(name = "total_exp")
    private String totalExp;

}