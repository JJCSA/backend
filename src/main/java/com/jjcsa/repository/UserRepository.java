package com.jjcsa.repository;

import com.jjcsa.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    User findUserById(UUID id);

    User findUserByEmail(String email);
}
