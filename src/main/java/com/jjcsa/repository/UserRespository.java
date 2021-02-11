package com.jjcsa.repository;

import com.jjcsa.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRespository extends CrudRepository<User, UUID> {
    Optional<User> findById(UUID id);
}
