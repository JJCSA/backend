package com.jjcsa.controller.admin;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.service.ActionService;
import com.jjcsa.service.UserProfileService;
import com.jjcsa.service.UserService;
import com.jjcsa.util.KeycloakUtil;
import com.jjcsa.util.UserUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin/users", produces = "application/json")
public class AdminUserController {

    private final UserMapper userMapper;

    private final UserProfileService userProfileService;

    private final UserService userService;

    private final ActionService actionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PutMapping(path = "/updateUserRole")
    public ResponseEntity<String> updateUserRole(
            @RequestParam("userEmail") @NonNull final String userEmail,
            @RequestParam("role") @NonNull final String updatedRole,
            @RequestParam("action") @NonNull final String action,
            KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken accessToken = account.getKeycloakSecurityContext().getToken();
        Access resourceAccess =
                accessToken.getResourceAccess(KeycloakUtil.JJCSA_CLIENT_ID);

        Set<String> roles = resourceAccess.getRoles();
        log.info("Roles {}",roles);

        if(!roles.contains(UserRole.SuperAdmin.name())){
            return new ResponseEntity<>("User does not have valid permissions", HttpStatus.FORBIDDEN);
        }

        String userRole = null;

        if(UserUtil.isValidRole(updatedRole)) {
            userRole = UserRole.valueOf(updatedRole).name(); // Get the correct case and role value from enum
        } else {
            throw new BadRequestException("Invalid role type",
                    "Role type: " + updatedRole + " does not exist",
                    "",
                    "",
                    "");
        }

        log.info("Superadmin: {} is updating user: {} role to {}", accessToken.getEmail(), userEmail, userRole);

        try {
            KeycloakUtil.updateUserRole(userRole, userEmail, action);
        } catch (Exception ex) {
            log.error("Error while updating user: {} with role: {}", userEmail, updatedRole);
            throw ex;
        }

        try {
            actionService.saveAction(userEmail,
                    accessToken.getEmail(),
                    action);
        } catch (Exception e) {
            log.error("Error while action details", e);
            return new ResponseEntity<>("Exception while adding action details", HttpStatus.FAILED_DEPENDENCY);
        }

        return new ResponseEntity<>("user role updated successfully", HttpStatus.NO_CONTENT);

    }

    @PutMapping(path = "/updateUserDetails")
    public ResponseEntity<String> updateUserDetails(
            @RequestParam("userProfileDetails") @NonNull final String userProfileJSONString,
            @RequestParam("action") @NonNull final String action,
            KeycloakAuthenticationToken authenticationToken)
            throws JsonProcessingException {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken accessToken = account.getKeycloakSecurityContext().getToken();
        Access resourceAccess =
                accessToken.getResourceAccess(KeycloakUtil.JJCSA_CLIENT_ID);

        Set<String> roles = resourceAccess.getRoles();
        log.info("Roles {}",roles);

        if(!roles.contains(UserRole.Admin.name())){
            return new ResponseEntity<>("User does not have valid permissions", HttpStatus.FORBIDDEN);
        }

        User existingUserDetails = UserUtil.getUserDetailsToBeUpdated(
                userProfileJSONString, userMapper, userService, objectMapper
        );

        try {
            userProfileService.updateUserProfile(existingUserDetails);
        } catch (Exception e) {
            log.error("Error while updating user", e);
            return new ResponseEntity<>("Exception while updating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            actionService.saveAction(existingUserDetails.getEmail(),
                    accessToken.getEmail(),
                    action);
        } catch (Exception e) {
            log.error("Error while action details", e);
            return new ResponseEntity<>("Exception while adding action details", HttpStatus.FAILED_DEPENDENCY);
        }

        return new ResponseEntity<>("UserProfile updated successfully", HttpStatus.NO_CONTENT);
    }

    /**
     * Delete user method to delete the user with {userId} provided as param.
     * @param userId user to be deleted. It will be email id of the user.
     * @returns the message on successful deletion of user
     */
    @DeleteMapping(path = "/{userId}")
    public String deleteUser(@PathVariable String userId){

        log.info("Delete User invoked for userId:{}", userId);
        User user = userService.getUser(userId);
        userService.deleteUser(user);
        return "User successfully deleted";
    }

    @GetMapping(path = "")
    public List<User> getUsersList() {
        log.info("Getting User List");
        return userService.getAllUsers();
    }
}
