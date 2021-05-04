package com.jjcsa.controller;

import java.security.Principal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.service.UserService;
import com.jjcsa.util.KeycloakUtil;

import com.jjcsa.util.UserUtil;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/users", produces = "application/json")
public class LoginController {

    private final UserService userService;
    private final UserMapper userMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    // This method is for test
    @GetMapping(path = "/test")
    public String testUserLogin(@NonNull final Principal principal) {
        String username = "";
        if (principal instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
            AccessToken token = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken();
            username = token.getPreferredUsername();
        }
        return "hello " + username;
    }

    @GetMapping(path = "/test2")
    public String test2() {
        return "test2";
    }

    // This method is for test
//    @PostMapping(path = "/login")
//    public String login(@RequestBody @NonNull final User user) {
//
//        if(user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
//            throw new IllegalArgumentException("Email or password is empty. Username and password are mandatory");
//        }
//        log.info("Getting token for '{}' ...", user.getEmail());
//        final Keycloak keycloakMovieApp = KeycloakBuilder.builder().serverUrl(keycloakServerUrl)
//                .realm(KeycloakUtil.JJCSA_REALM_NAME).username(user.getEmail()).password(user.getPassword())
//                .clientId(KeycloakUtil.JJCSA_CLIENT_ID).build();
//        final String token = keycloakMovieApp.tokenManager().getAccessToken().getToken();
//        log.info("'{}' logged in successfully", user.getEmail());
//        return token;
//    }

    @PostMapping(path = "/register")
    public ResponseEntity<AddNewUser> register(
            @RequestParam("newUser") @NonNull final String newUserJSONString,
            @RequestParam("jainProof") @NonNull final MultipartFile jainProofDoc,
            @RequestParam("profPicture") @NonNull final MultipartFile profPicture) throws JsonProcessingException {

        AddNewUser addNewUser = objectMapper.readValue(newUserJSONString, AddNewUser.class);

        // Save the new user in our db
        final User user = userMapper.toUserProfile(addNewUser);
        user.setUserStatus(UserStatus.Pending);

        log.info("Saving user for '{}' ...", addNewUser.getEmail());
        userService.saveUser(user, jainProofDoc, profPicture);
        log.info("UserProfile [{}] stored successfully", user);
        log.info(user.toString());

        // Create the new user in keycloak
        boolean userCreatedInKeycloak = false;
        try {
            userCreatedInKeycloak = KeycloakUtil.createNewUser(addNewUser);
        } catch (BadRequestException e) {
            userCreatedInKeycloak = false;
            throw e;
        } finally {
            if(!userCreatedInKeycloak) {
                log.error("Creating User in Keycloak failed! Deleting from our db");
                // Delete the record from our db
                userService.deleteUserInDbOnly(user);
            }
        }

        return new ResponseEntity<>(addNewUser, HttpStatus.CREATED);
    }

    @PutMapping(path = "/updateUserRole")
    public ResponseEntity<String> updateUserRole(
            @RequestParam("userEmail") @NonNull final String userEmail,
            @RequestParam("actionPerformerEmail") @NonNull final String actionPerformerEmail,
            @RequestParam("role") @NonNull final String updatedRole,
            @RequestParam("action") @NonNull final String action,
            KeycloakAuthenticationToken authenticationToken)
            throws JsonProcessingException{
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        if(token.isActive()){
            log.info("Updating user role with email {}", userEmail);
            User actionPerfomerDetails = userService.getUser(actionPerformerEmail);

            // User who is performing action should be an approved user
            if(!UserUtil.isUserApproved(actionPerfomerDetails)){
                return new ResponseEntity<>("Action performing user is not an active user", HttpStatus.UNAUTHORIZED);
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

            try {
                KeycloakUtil.updateUserRole(userRole, userEmail, action);
            } catch (Exception ex) {
                log.error("Error while updating user: {} with role: {}", userEmail, updatedRole);
                throw ex;
            }

            return new ResponseEntity<>("user role updated successfully", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>("User does not have valid token", HttpStatus.UNAUTHORIZED);
        }

    }
}
