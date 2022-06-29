package com.jjcsa.repository;

import com.jjcsa.model.UserTempPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTempPasswordRepository extends JpaRepository<UserTempPassword, String> {
    UserTempPassword findByEmail(String email);
}
