package com.jjcsa.controller;

import java.security.Principal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserLoginMapper;
import com.jjcsa.mapper.UserProfileMapper;
import com.jjcsa.model.UserLogin;
import com.jjcsa.model.UserProfile;
import com.jjcsa.service.UserLoginService;
import com.jjcsa.service.UserProfileService;
import com.jjcsa.util.KeycloakUtil;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(path="/api/users", produces = "application/json")
public class UserController {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserLoginMapper userLoginMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(path = "/getUserDetails")
    public String getUserDetails(@NonNull final Principal principal) {
        log.info("Fetching user detail..");
        String username = "";
        if (principal instanceof KeycloakAuthenticationToken) {
            KeycloakAuthenticationToken keycloakAuthenticationToken = (KeycloakAuthenticationToken) principal;
            AccessToken token = keycloakAuthenticationToken.getAccount().getKeycloakSecurityContext().getToken();
            username = token.getPreferredUsername();
        }
        return "hello " + username;
    }

    @PostMapping(path = "/login")
    public String login(@RequestBody @NonNull final UserLogin userLogin) {

        if(userLogin.getEmail().isEmpty() || userLogin.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Email or password is empty. Username and password are mandatory");
        }
        log.info("Getting token for '{}' ...", userLogin.getEmail());
        final Keycloak keycloakMovieApp = KeycloakBuilder.builder().serverUrl(keycloakServerUrl)
                .realm(KeycloakUtil.JJCSA_REALM_NAME).username(userLogin.getEmail()).password(userLogin.getPassword())
                .clientId(KeycloakUtil.JJCSA_CLIENT_ID).build();
        final String token = keycloakMovieApp.tokenManager().getAccessToken().getToken();
        log.info("'{}' logged in successfully", userLogin.getEmail());
        return token;
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AddNewUser> register(
            @RequestParam("newUser") @NonNull final String newUserJSONString,
            @RequestParam("jainProof") @NonNull final MultipartFile jainProofDoc) throws JsonProcessingException {

        AddNewUser addNewUser = objectMapper.readValue(newUserJSONString, AddNewUser.class);

        // Save the new user in our db
        UserLogin userLogin = userLoginMapper.toUserLogin(addNewUser);
        final UserProfile userProfile = userProfileMapper.toUserProfile(addNewUser);

        log.info("Saving userLogin for '{}' ...", addNewUser.getEmail());
        userLogin = userLoginService.saveNewUserLogin(userLogin);
        log.info("UserLogin stored successfully");

        log.info("Saving userProfile for '{}' ...", addNewUser.getEmail());
        userProfile.setUserLogin(userLogin);
        log.info("UserProfile [{}] stored successfully", userProfile);
        log.info(userProfile.toString());
        userProfileService.saveUserProfile(userProfile, jainProofDoc);

        // Create the new user in keycloak
        boolean userCreatedInKeycloak = false;
        try {
            userCreatedInKeycloak = KeycloakUtil.createNewUser(addNewUser, keycloakServerUrl);
        } catch (BadRequestException e) {
            userCreatedInKeycloak = false;
            throw e;
        } finally {
            if(!userCreatedInKeycloak) {
                log.error("Creating User in Keycloak failed! Deleting from our db");
                // Delete the record from our db
                userProfileService.deleteUserProfile(userProfile);
            }
        }

        return new ResponseEntity<>(addNewUser, HttpStatus.CREATED);
    }

}
