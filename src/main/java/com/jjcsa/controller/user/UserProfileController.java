package com.jjcsa.controller.user;

import com.jjcsa.dto.UpdateUserPictureDto;
import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.EmailEvent;
import com.jjcsa.service.*;
import com.jjcsa.util.ApplicationConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import javax.validation.Valid;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;
    private final AWSS3Service awss3Service;
    private final KeycloakService keycloakService;
    private final EmailSenderService emailSenderService;

    @GetMapping()
    public UserProfile getUserProfile(KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        return userProfileService.getUserProfile(token.getSubject());
    }

    @PutMapping()
    public UserProfile updateUserProfile(@RequestBody @Valid UserProfile userProfile, KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        if(!token.getEmail().equalsIgnoreCase(userProfile.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update email address");
        }

        return userProfileService.updateUserProfile(token.getSubject(), userProfile);
    }

    @PutMapping("/profPicture")
    public UserProfile updateUserProfilePicture(@RequestBody @Valid UpdateUserPictureDto updateUserPictureDto,
                                                KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User user = userService.getUserById(token.getSubject());
        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find user");
        }
        
        return userProfileService.updateUserProfilePicture(user, updateUserPictureDto.getProfPicture());
    }

    @GetMapping("/profPicture")
    public byte[] getProfilePicture(KeycloakAuthenticationToken authenticationToken){
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User user = userService.getUserById(token.getSubject());
        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find user");
        }
        if(!token.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update profile picture for another User");
        }
        return awss3Service.getImageFromS3(user.getId(), ApplicationConstants.FILE_TYPE_PROFILE);
    }

    @PostMapping("/updatePassword")
    public Boolean updateUserPassword(@RequestParam String oldPassword,
                                      @RequestParam String newPassword,
                                      KeycloakAuthenticationToken authenticationToken) {

        if (oldPassword.equalsIgnoreCase(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password cannot be same as old password");
        }

        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User user = userService.getUserById(token.getSubject());
        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find user");
        }

        boolean pwUpdated = keycloakService.verifyAndUpdateUserPassword(user, oldPassword, newPassword);
        emailSenderService.sendEmail(user, pwUpdated ? EmailEvent.PW_UPDATE : EmailEvent.PW_UPDATE_FAILED);

        return pwUpdated;
    }

}
