package com.jjcsa.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.dto.UserProfile;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.User;
import com.jjcsa.service.ActionService;
import com.jjcsa.service.UserProfileService;
import com.jjcsa.service.UserService;
import com.jjcsa.util.KeycloakUtil;
import com.jjcsa.util.UserUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Secured("ROLE_USER")
@RequiredArgsConstructor
@RequestMapping(path="/api/users", produces = "application/json")
public class UserProfileController {

    private final UserMapper userMapper;

    private final UserProfileService userProfileService;

    private final UserService userService;

    private final ActionService actionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(path = "/profile")
    public UserProfile getUserProfile(KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        return userProfileService.getUserProfile(token.getEmail());
    }

    @PutMapping(path = "/updateUserDetails")
    public ResponseEntity<String> updateUserDetails(
            @RequestParam("userProfileDetails") @NonNull final String userProfileJSONString,
            @RequestParam("actionPerformerEmail") @NonNull final String actionPerformerEmail,
            @RequestParam("action") @NonNull final String action,
            KeycloakAuthenticationToken authenticationToken)
            throws JsonProcessingException{
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        if(token.isActive()){
            UserProfile updatedUserProfile = objectMapper.readValue(userProfileJSONString, UserProfile.class);
            log.info("Updating user with email {}", updatedUserProfile.getEmail());
            User userDetailsToBeUpdated = userMapper.toUser(updatedUserProfile);
            User existingUserDetails = userService.getUser(userDetailsToBeUpdated.getEmail());
            User actionPerfomerDetails = userService.getUser(actionPerformerEmail);

            // User who is performing action should be an approved user
            if(!UserUtil.isUserApproved(actionPerfomerDetails)){
                return new ResponseEntity<>("Action performing user is not an active user", HttpStatus.UNAUTHORIZED);
            }

            // If user whose details need to be updated is in pending status then only admin can update that user
            if(!UserUtil.isUserPending(existingUserDetails) && !KeycloakUtil.isAdmin(account)){
                return new ResponseEntity<>("Only admin can perform update action on pending user", HttpStatus.UNAUTHORIZED);
            }

            // If there is a change in role then only superadmin can perform that
            if(!UserUtil.isUserPending(existingUserDetails) && !KeycloakUtil.isAdmin(account)){
                return new ResponseEntity<>("Only admin can perform update action on pending user", HttpStatus.UNAUTHORIZED);
            }

            UserUtil.copyNonNullProperties(existingUserDetails, userDetailsToBeUpdated);
            userDetailsToBeUpdated.setId(existingUserDetails.getId());
            try {
                userProfileService.updateUserProfile(existingUserDetails);
            } catch (Exception e) {
                log.error("Error while updating user", e);
                return new ResponseEntity<>("Exception while updating user", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Don't update action table if the same user is updating self profile
            if(!userDetailsToBeUpdated.getEmail().equals(actionPerformerEmail)) {
                try {
                    actionService.saveAction(userDetailsToBeUpdated.getEmail(),
                            actionPerformerEmail,
                            action);
                } catch (Exception e) {
                    log.error("Error while action details", e);
                    return new ResponseEntity<>("Exception while adding action details", HttpStatus.FAILED_DEPENDENCY);
                }
            }

            return new ResponseEntity<>("UserProfile updated successfully", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("User does not have valid token", HttpStatus.UNAUTHORIZED);
        }

    }

}
