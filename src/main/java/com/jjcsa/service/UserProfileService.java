package com.jjcsa.service;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.repository.WorkExRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.isNull;

@Service
@Slf4j
@AllArgsConstructor
public class UserProfileService {

    private final UserService userService;
    private final EducationRepository educationRepository;
    private final WorkExRepository workExRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    public UserProfile getUserProfile(String email) {
        User user = userService.getUser(email);
        if(user == null)
            throw new BadRequestException(
                    "User Profile does not exist",
                    "User's profile has not been created",
                    "",
                    "",
                    ""
            );

        if(UserStatus.Pending.equals(user.getUserStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User is pending"
            );
        }

        user.setEducationList(educationRepository.findAllByUser(user));
        user.setWorkExperience(workExRepository.findAllByUser(user));

        UserRole userRole = keycloakService.getUserRole(email);

        return userProfileMapper.toUserProfile(user, userRole);
    }

    public UserProfile updateUserProfile(String userEmail, UserProfile updatedUserProfile) {
        User user = userService.getUser(userEmail);

        if(isNull(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not have a profile");

        // Validate User Status
        if(!updatedUserProfile.getUserStatus().equals(user.getUserStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot update UserStatus");
        }
        if(user.getUserStatus().equals(UserStatus.Pending)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pending User cannot update Profile");
        }

        // Update user
        user.setFirstName(updatedUserProfile.getFirstName());
        user.setMiddleName(updatedUserProfile.getMiddleName());
        user.setLastName(updatedUserProfile.getLastName());
        user.setMobileNumber(updatedUserProfile.getMobileNumber());
        user.setContactMethod(updatedUserProfile.getContactMethod());
        user.setCommunityName(updatedUserProfile.getCommunityName());
        user.setStreet(updatedUserProfile.getStreet());
        user.setCity(updatedUserProfile.getCity());
        user.setState(updatedUserProfile.getState());
        user.setZip(updatedUserProfile.getZip());
        user.setDateOfBirth(updatedUserProfile.getDateOfBirth());
        user.setSocialMediaPlatform(updatedUserProfile.getSocialMediaPlatform());
        user.setVolunteeringInterest(updatedUserProfile.getVolunteeringInterest());
        user.setLinkedinUrl(updatedUserProfile.getLinkedinUrl());
        user.setEducationList(updatedUserProfile.getEducation());
        user.setWorkExperience(updatedUserProfile.getWorkExperience());

        log.info("Updating User profile for email {}", user.getEmail());

        User savedUser = userRepository.save(user);

        UserRole userRole = keycloakService.getUserRole(userEmail);

        return userProfileMapper.toUserProfile(savedUser, userRole);
    }

    public UserProfile updateUserProfilePicture(User user, MultipartFile profPicture) {
        log.info("Updating User profile picture for email {}", user.getEmail());

        user.setProfilePicture(userService.saveProfilePictureForUserProfile(user, profPicture));
        User savedUser = userRepository.save(user);

        UserRole userRole = keycloakService.getUserRole(savedUser.getEmail());

        return userProfileMapper.toUserProfile(savedUser, userRole);
    }
}
