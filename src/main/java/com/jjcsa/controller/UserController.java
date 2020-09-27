package com.jjcsa.controller;

import java.security.Principal;

import com.jjcsa.model.UserLogin;
import com.jjcsa.util.KeycloakUtil;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path="/api/users", produces = "application/json")
public class UserController {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

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

}
