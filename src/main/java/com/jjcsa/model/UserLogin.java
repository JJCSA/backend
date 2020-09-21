package com.jjcsa.model;

import com.jjcsa.model.enumModel.UserRole;
import lombok.Data;

import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "user_login")
public @Data class UserLogin{

    @Id
    @Column(name="user_id")
    @GeneratedValue()
    private UUID id;
    
    private String email;
	private String password;
	
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userrole;
    
}