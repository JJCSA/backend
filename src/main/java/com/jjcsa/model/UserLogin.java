package com.jjcsa.model;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "user_login")
public class UserLogin{

    @Id
    @Column(name="user_id")
    @GeneratedValue()
    private UUID id;
    
    private String email;
	private String password;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userrole = UserRole.Pending;

	public UUID getId() {
		return this.id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRole getUserrole() {
		return this.userrole;
	}

	public void setUserrole(UserRole userrole) {
		this.role = userrole;
	}

    
}