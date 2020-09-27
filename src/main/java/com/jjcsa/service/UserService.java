package com.jjcsa.service;

import com.jjcsa.model.User;
import com.jjcsa.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
@Service
public class UserService{

    @Autowired
    private UserRespository userRepository;

    public User getUser(long id) {
        Optional<User> user = userRepository.findById(id);
        return null;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getall()
    {
        return (List<User>)userRepository.findAll();
    }
}
