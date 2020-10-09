package com.jjcsa.service;

import com.jjcsa.exception.UserAlreadyExistsException;
import com.jjcsa.model.UserLogin;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.repository.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserLoginService {

    @Autowired
    private UserLoginRepository userLoginRepository;

    public Optional<UserLogin> getUserLogin(String email) {
        return userLoginRepository.findByEmail(email);
    }

    public UserLogin saveNewUserLogin(UserLogin userLogin) {

        //Check if userLogin already exits
        if(getUserLogin(userLogin.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already present with this email address");
        }

        // Ensure userrole is set to User by default
        if(userLogin.getUserrole() == null) {
            userLogin.setUserrole(UserRole.User);
        }

        return userLoginRepository.save(userLogin);
    }
}
