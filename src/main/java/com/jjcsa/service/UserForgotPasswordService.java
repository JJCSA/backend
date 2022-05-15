package com.jjcsa.service;

import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.UserTempPassword;
import com.jjcsa.repository.UserTempPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserForgotPasswordService {
    private UserTempPasswordRepository userTempPasswordRepository;

    public Boolean generateTempPasswordForEmail(String email) {

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
                return false;
            }
        }

        // generate new temp password


        // send user email with temp password

        return true;
    }

}
