package com.jjcsa.repository;

import com.jjcsa.model.UserLogin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserLoginRepository extends CrudRepository<UserLogin, UUID> {
    Optional<UserLogin> findByEmail(String email);
}
