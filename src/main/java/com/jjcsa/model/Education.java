package com.jjcsa.model;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.DateTimeFormat;

@Entity
@Table(name = "education")
public class Education{

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

	public int getEducationId() {
		return this.educationId;
	}

	public void setEducationId(int educationId) {
		this.educationId = educationId;
	}

	public String getUniversityName() {
		return this.universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	public int getGradMonth() {
		return this.gradMonth;
	}

	public void setGradMonth(int gradMonth) {
		this.gradMonth = gradMonth;
	}

	public int getGradYear() {
		return this.gradYear;
	}

	public void setGradYear(int gradYear) {
		this.gradYear = gradYear;
	}

	public String getSpecialization() {
		return this.specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getDegree() {
		return this.degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

}