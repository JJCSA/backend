package com.jjcsa.controller;
import java.util.List;

import com.jjcsa.model.User;
import com.jjcsa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/users", produces = "application/json")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/users")
    public User saveUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return savedUser;
    }

    @GetMapping(path = "/test")
    public String testApi() {
        return "{ \"message\": \"Test Successful\"}";
    }

    @GetMapping(path = "/userList")
    public List<User> getUser() {
        List<User> savedUser = userService.getall();
        return savedUser;
    }

}
