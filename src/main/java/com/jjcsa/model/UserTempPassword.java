package com.jjcsa.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_temp_password")
@Data
public class UserTempPassword {
    @Id
    private String id;

    private String email;

    @Column(name = "temp_password")
    private String tempPassword;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
}
