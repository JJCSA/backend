package com.jjcsa.service;

import com.jjcsa.model.User;
import com.jjcsa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService{
    public final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(long id) {
        Optional<User> user = userRepository.findById(id);
        return null;
    }
}
