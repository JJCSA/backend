package com.jjcsa.controller.user;

import com.jjcsa.dto.UserProfile;
import com.jjcsa.model.User;
import com.jjcsa.service.UserProfileService;
import com.jjcsa.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

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
        return userProfileService.getUserProfile(token.getEmail());
    }

    @PutMapping()
    public UserProfile updateUserProfile(@RequestBody UserProfile userProfile, KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        if(!token.getEmail().equalsIgnoreCase(userProfile.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot update email address");
        }

        UserProfile updatedUserProfile = userProfileService.updateUserProfile(token.getEmail(), userProfile);

        return updatedUserProfile;
    }

    @PutMapping("/{userId}/profPicture")
    public UserProfile updateUserProfilePicture(@PathVariable("userId") UUID userId,
                                                @RequestParam("profPicture") MultipartFile profPicture,
                                                KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User user = userService.getUserById(userId);
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
