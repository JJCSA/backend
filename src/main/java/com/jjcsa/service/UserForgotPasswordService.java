package com.jjcsa.service;

import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.UserTempPassword;
import com.jjcsa.repository.UserRepository;
import com.jjcsa.repository.UserTempPasswordRepository;
import com.jjcsa.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserForgotPasswordService {
    private UserTempPasswordRepository userTempPasswordRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public Boolean generateTempPasswordForEmail(String email) {

        //check if email exists
        if(!userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email does not exist",
                    "No account found with the entered email",
                    "",
                    "",
                    "");
        }

        // check if user already requested for password
        UserTempPassword oldUserTempPassword = userTempPasswordRepository.findByEmail(email);
        if (nonNull(oldUserTempPassword)) {
            // check if previous password is expired?
            if (oldUserTempPassword.getExpirationTime().isBefore(LocalDateTime.now())) {
                // old userTempPassword is expired
                userTempPasswordRepository.delete(oldUserTempPassword);
            } else {
                throw new BadRequestException("User already requested for new password, cannot generate another one",
                        "Please check email for temp password",
                        "",
                        "",
                        "");
            }
        }

        // generate new temp password
        String rawPw = StringUtil.generateRandomString(6);

        // TODO: send user email with temp password

        // Save temp password to db
        UserTempPassword newTempPassword = new UserTempPassword();
        newTempPassword.setEmail(email);
        newTempPassword.setCreatedTime(LocalDateTime.now());
        newTempPassword.setExpirationTime(LocalDateTime.now().plusHours(1)); //expiration 1 hour from now
        newTempPassword.setTempPassword(bCryptPasswordEncoder.encode(rawPw));

        userTempPasswordRepository.save(newTempPassword);

        return true;
    }

    public Boolean changeUserPasswordForTempPassword(String email, String tempPassword) {

        // check if user requested for password change
        UserTempPassword userTempPassword = userTempPasswordRepository.findByEmail(email);
        if(isNull(userTempPassword)) {
            throw new BadRequestException("User did not request for password change",
                    "Please request password change first",
                    "",
                    "",
                    "");
        }

        // check if temp password is expired?
        if(userTempPassword.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Temp password expired",
                    "Please request password change again.",
                    "",
                    "",
                    "");
        }

        // check if temp password matches
        if(!bCryptPasswordEncoder.matches(tempPassword, userTempPassword.getTempPassword())) {
            throw new BadRequestException("Incorrect temp password",
                    "Please enter correct password sent to your email.",
                    "",
                    "",
                    "");
        }

        //TODO: call keycloak to change password for email

        // Delete temp password record
        userTempPasswordRepository.delete(userTempPassword);

        return true;
    }

}
