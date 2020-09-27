package com.jjcsa.repository;

import com.jjcsa.model.User;
import com.jjcsa.model.UserProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRespository extends CrudRepository<UserProfile, UUID> {
    Optional<UserProfile> findById(UUID id);
}
