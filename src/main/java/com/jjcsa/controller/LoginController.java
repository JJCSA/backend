package com.jjcsa.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.EmailTemplate;
import com.jjcsa.service.KeycloakService;
import com.jjcsa.service.EmailSenderService;
import com.jjcsa.service.UserService;

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
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;
    private final EmailSenderService emailSenderService;

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

    @GetMapping(path="/send-email")
    public String sendEmail(){
        User user = User.builder().firstName("Developer").lastName("Dev").build();
        int failed = emailSenderService.sendEmail(user,"newsletter@jjcsausa.com", "REGISTRATION",
                Collections.singletonList("jjcsausawebdev@gmail.com"), Collections.EMPTY_LIST,Collections.EMPTY_LIST);
        return "Sent email failed: " + failed;
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

        User user = userService.saveUser(addNewUser, jainProofDoc, profPicture);

        emailSenderService.sendEmail(user,"newsletter@jjcsausa.com", EmailTemplate.REGISTRATION.name(),Collections.singletonList(user.getEmail())
                ,Collections.singletonList("jjcsausawebdev@gmail.com"),Collections.EMPTY_LIST);
        return new ResponseEntity<>(addNewUser, HttpStatus.CREATED);
    }

}
