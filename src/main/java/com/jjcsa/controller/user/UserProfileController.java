package com.jjcsa.controller.user;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.User;
import com.jjcsa.service.UserProfileService;
import com.jjcsa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserService userService;

    @GetMapping()
    public UserProfile getUserProfile(KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        return userProfileService.getUserProfile(token.getSubject());
    }

    @PutMapping()
    public UserProfile updateUserProfile(@RequestBody UserProfile userProfile, KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        if(!token.getEmail().equalsIgnoreCase(userProfile.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update email address");
        }

        UserProfile updatedUserProfile = userProfileService.updateUserProfile(token.getSubject(), userProfile);

        return updatedUserProfile;
    }

    @PutMapping("/profPicture")
    public UserProfile updateUserProfilePicture(@RequestParam("profPicture") MultipartFile profPicture,
                                                KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User user = userService.getUserById(token.getSubject());
        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find user");
        }
        if(!token.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update profile picture for another User");
        }

        UserProfile updatedUserProfile = userProfileService.updateUserProfilePicture(user, profPicture);

        return updatedUserProfile;
    }

}
