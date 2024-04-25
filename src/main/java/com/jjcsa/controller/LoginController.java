package com.jjcsa.controller;

import java.security.Principal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.dto.AddNewUser;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.EmailEvent;
import com.jjcsa.service.NewEmailService;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/users", produces = "application/json")
public class LoginController {

    private final UserService userService;
    private final NewEmailService newEmailSenderService;

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

    @PostMapping(path = "/register")
    public ResponseEntity<AddNewUser> register(
            @RequestParam("newUser") @NotBlank(message = "newUser should not be blank") final String newUserJSONString,
            @RequestParam(value = "jainProof", required = false) @NotNull(message = "jainProofDoc cannot be null") final MultipartFile jainProofDoc,
            @RequestParam("profPicture") @NotNull(message = "profPicture cannot be null") final MultipartFile profPicture) throws JsonProcessingException {

        AddNewUser addNewUser = objectMapper.readValue(newUserJSONString, AddNewUser.class);

        User user = userService.saveUser(addNewUser, jainProofDoc, profPicture);

        newEmailSenderService.sendEmail(EmailEvent.REGISTRATION, user, "");
        return new ResponseEntity<>(addNewUser, HttpStatus.CREATED);
    }
}
