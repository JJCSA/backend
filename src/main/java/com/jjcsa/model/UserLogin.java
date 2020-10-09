package com.jjcsa.model;

import com.jjcsa.model.enumModel.UserRole;
import lombok.Data;

import java.util.UUID;
import javax.persistence.*;

@Entity
@Table(name = "user_login")
public @Data class UserLogin{

    @Id
    @Column(name="user_id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", columnDefinition = "varchar(45) default User")
    private UserRole userrole;

    public UserLogin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}