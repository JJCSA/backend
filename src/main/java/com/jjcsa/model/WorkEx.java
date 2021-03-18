package com.jjcsa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "work_ex")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class WorkEx {

    @Id
    @Column(name = "exp_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int expId;

    @Column(name = "company_name", columnDefinition = "varchar(45) default ''")
    private String companyName;

    @Column(columnDefinition = "varchar(45) default ''")
    private String role;

    @Column(columnDefinition = "varchar(45) default ''")
    private String location;

    @Column(name = "total_exp", columnDefinition = "varchar(5) default ''")
    private String totalExp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}