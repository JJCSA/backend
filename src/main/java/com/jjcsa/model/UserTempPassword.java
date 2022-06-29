package com.jjcsa.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_temp_password")
@Data
public class UserTempPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String email;

    @Column(name = "temp_password")
    private String tempPassword;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
}
