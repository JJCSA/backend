package com.jjcsa.model;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(name = "work_ex")
public class WorkEx{

    @Id
    @Column(name="exp_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int expId;

    @Column(name="company_name")
    private String companyName;
    
    
    private String role;
    private String location;
    
    @Column(name="total_exp")
    private String totalExp;

	public int getExpId() {
		return this.expId;
	}

	public void setExpId(int expId) {
		this.expId = expId;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTotalExp() {
		return this.totalExp;
	}

	public void setTotalExp(String totalExp) {
		this.totalExp = totalExp;
	}


}