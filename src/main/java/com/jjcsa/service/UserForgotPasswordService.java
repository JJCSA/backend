package com.jjcsa.service;

import com.jjcsa.dto.UserResetPassword;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.model.User;
import com.jjcsa.model.UserTempPassword;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.repository.UserTempPasswordRepository;
import com.jjcsa.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserForgotPasswordService {
    private final UserTempPasswordRepository userTempPasswordRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final KeycloakService keycloakService;
    private final EmailSenderService emailSenderService;

    @Value("${frontend.forgotPasswordURL}")
    private String forgotPasswordURL;

    public Boolean generateTempPasswordForEmail(String email) {

        //check if email exists
        if(!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not exist");

        }

        // check if user already requested for password
        UserTempPassword oldUserTempPassword = userTempPasswordRepository.findByEmail(email);
        if (nonNull(oldUserTempPassword)) {
            // check if previous password is expired?
            if (oldUserTempPassword.getExpirationTime().isBefore(LocalDateTime.now())) {
                // old userTempPassword is expired
                userTempPasswordRepository.delete(oldUserTempPassword);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already requested for new password, cannot generate another one");

            }
        }

        // generate new temp password
        String rawPw = StringUtil.generateRandomString(6);
        System.out.println("generated random pw: " + rawPw);

        String resetPasswordLink = StringUtil.generateForgotPasswordLink(forgotPasswordURL, email, rawPw);
        emailSenderService.sendEmailForForgotPassword(email, resetPasswordLink);

        // Save temp password to db
        UserTempPassword newTempPassword = new UserTempPassword();
        newTempPassword.setEmail(email);
        newTempPassword.setCreatedTime(LocalDateTime.now());
        newTempPassword.setExpirationTime(LocalDateTime.now().plusHours(1)); //expiration 1 hour from now
        newTempPassword.setTempPassword(bCryptPasswordEncoder.encode(rawPw));

        userTempPasswordRepository.save(newTempPassword);

        return true;
    }

    public Boolean changeUserPasswordForTempPassword(UserResetPassword userResetPassword) {

        String email = userResetPassword.getEmail();
        String tempPassword = userResetPassword.getTempPassword();
        String newPassword = userResetPassword.getNewPassword();

        //check if email exists
        User user = userRepository.findUserByEmail(email);
        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email does not exist");
        }

        // check if user requested for password change
        UserTempPassword userTempPassword = userTempPasswordRepository.findByEmail(email);
        if(isNull(userTempPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User did not request for password change");
        }

        // check if temp password is expired?
        if(userTempPassword.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Temp password expired");
        }

        // check if temp password matches
        if(!bCryptPasswordEncoder.matches(tempPassword, userTempPassword.getTempPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect temp password");
        }

        // Update password in keycloak
        Boolean passwordChanged = keycloakService.resetUserPassword(user.getId(), newPassword);
        if (!passwordChanged) {
            throw new UnknownServerErrorException("Unable to reset password",
                    "Resetting password in Keycloak failed",
                    "",
                    "",
                    "");
        }

        // Delete temp password record
        userTempPasswordRepository.delete(userTempPassword);

        return true;
    }

}
