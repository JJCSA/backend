package com.jjcsa.service;

import com.jjcsa.model.UserProfile;
import com.jjcsa.repository.UserProfileRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRespository userProfileRepository;

    public UserProfile getUserProfile(UUID id) {
        Optional<UserProfile> user = userProfileRepository.findById(id);
        return null;
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}
