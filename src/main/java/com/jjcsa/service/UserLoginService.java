package com.jjcsa.service;

import com.jjcsa.exception.BadRequestException;
import com.jjcsa.exception.UnknownServerErrorException;
import com.jjcsa.model.UserLogin;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.repository.UserLoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserLoginService {

    @Autowired
    private UserLoginRepository userLoginRepository;

    public Optional<UserLogin> getUserLogin(String email) {
        return userLoginRepository.findByEmail(email);
    }

    public UserLogin saveNewUserLogin(UserLogin userLogin) {

        //Check if userLogin already exits
        if (getUserLogin(userLogin.getEmail()).isPresent()) {
            log.error("User [{}] already exists!", userLogin);
            throw new BadRequestException("User already exists",
                    "User with this email address already exists",
                    "Please login",
                    "Please try logging in with this email address",
                    "");
        }

        // Ensure userrole is set to User by default
        if (userLogin.getUserrole() == null) {
            userLogin.setUserrole(UserRole.User);
        }
        try{
            userLogin = userLoginRepository.save(userLogin);
        }
        catch (Exception e) {
            log.error("Error while saving UserLogin");
            log.error(e.getMessage());
            throw new UnknownServerErrorException("Internal Server Error",
                    "Unable to store user to database",
                    "Please check for system outage",
                    "Please try again after some time",
                    "");
        }

        return userLogin;
    }
}
