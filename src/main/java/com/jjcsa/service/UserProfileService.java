package com.jjcsa.service;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.EducationRepository;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.repository.WorkExRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;

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
    private final AWSS3Service awss3Service;

    private final static Set<String> VOLUNTEERINGINTEREST = Set.of("WEBSITE","MARKETING","STUDENTWELFARE","ALUMNIWELFARE","ADMIN");

    public UserProfile getUserProfile(String userId) {
        User user = userService.getUserById(userId);
        if(user == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Profile does not exist");

        if(UserStatus.Pending.equals(user.getUserStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "User is pending"
            );
        }

//        user.setEducationList(educationRepository.findAllByUser(user));
//        user.setWorkExperience(workExRepository.findAllByUser(user));

        UserRole userRole = keycloakService.getUserRole(user.getId());
        if(isNull(userRole)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
        }

        String profileS3Url = awss3Service.generateSignedURLFromS3(user.getProfilePicture());
        log.info("Generated Pre Signed URL for Profile: {}", profileS3Url);

        return userProfileMapper.toUserProfile(user, userRole, profileS3Url);
    }

    public UserProfile updateUserProfile(String userId, UserProfile updatedUserProfile) {
        User user = userService.getUserById(userId);

        if(isNull(user)) throw new BadRequestException("User does not have a profile", "", "", "", "");

        // Validate User Status
        if(!updatedUserProfile.getUserStatus().equals(user.getUserStatus())) {
            throw new BadRequestException("User cannot update UserStatus", "", "", "", "");
        }
        if(user.getUserStatus().equals(UserStatus.Pending)) {
            throw new BadRequestException("Pending User cannot update Profile", "", "", "", "");
        }

        // Validate Gender
        if (user.getGender() != null && !user.getGender().equals(updatedUserProfile.getGender())) {
            // once set cannot update gender
            throw new BadRequestException("Cannot update gender once set", "", "", "", "");
        }

        // Validate Volunteering Interest
        if(StringUtils.isNotEmpty(updatedUserProfile.getVolunteeringInterest())){
            for (String v : updatedUserProfile.getVolunteeringInterest().split(",")) {
                log.info("V {}", v);
                if (!VOLUNTEERINGINTEREST.contains(v)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volunteering Interest not valid");
                }
            }
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
        user.setCountry(updatedUserProfile.getCountry());
        user.setZip(updatedUserProfile.getZip());
        user.setDateOfBirth(updatedUserProfile.getDateOfBirth());
        user.setSocialMediaPlatform(updatedUserProfile.getSocialMediaPlatform());
        user.setVolunteeringInterest(updatedUserProfile.getVolunteeringInterest());
        user.setLinkedinUrl(updatedUserProfile.getLinkedinUrl());
        user.setUserStudent(updatedUserProfile.getUserStudent());
        user.setAboutMe(updatedUserProfile.getAboutMe());
        user.setGender(updatedUserProfile.getGender());

        if (!CollectionUtils.isEmpty(updatedUserProfile.getEducation())) {
            updatedUserProfile.getEducation().forEach(e -> e.setUser(user));
            user.setEducationList(updatedUserProfile.getEducation());
        }

        if (!CollectionUtils.isEmpty(updatedUserProfile.getWorkExperience())) {
            updatedUserProfile.getWorkExperience().forEach(w -> w.setUser(user));
            user.setWorkExperience(updatedUserProfile.getWorkExperience());
        }

        // If user has status NewUser and has updated all the required fields, change status to Active
        if (UserStatus.NewUser.equals(user.getUserStatus())
                && userService.hasUserCompletedOnboardingProfile(user)
        ) {
            log.info("User with email {} completed their profile, changing UserStatus to Active", user.getEmail());
            user.setUserStatus(UserStatus.Active);
        }

        log.info("Updating User profile for email {}", user.getEmail());

        User savedUser = userRepository.save(user);

        UserRole userRole = keycloakService.getUserRole(user.getId());
        if(isNull(userRole)) {
            throw new BadRequestException("User not found in Keycloak", "", "", "", "");
        }

        String profileS3Url = awss3Service.generateSignedURLFromS3(savedUser.getProfilePicture());
        log.info("Generated pre signed URL :{} for user :{}", profileS3Url, savedUser.getFirstName());
        return userProfileMapper.toUserProfile(savedUser, userRole, profileS3Url);
    }

    public UserProfile updateUserProfilePicture(User user, MultipartFile profPicture) {
        log.info("Updating User profile picture for email {}", user.getEmail());

        user.setProfilePicture(userService.saveProfilePictureForUserProfile(user, profPicture));
        User savedUser = userRepository.save(user);

        UserRole userRole = keycloakService.getUserRole(user.getId());
        if(isNull(userRole)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
        }

        String profileS3Url = awss3Service.generateSignedURLFromS3(savedUser.getProfilePicture());
        log.info("Generated pre signed URL :{} for user :{}", profileS3Url, savedUser.getFirstName());

        return userProfileMapper.toUserProfile(savedUser, userRole, profileS3Url);
    }
}
