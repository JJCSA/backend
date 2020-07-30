package com.jjcsa.controller;

import com.jjcsa.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${endpoint.users}", produces="application/json")
public class UserController {
    private UserService userService;

}
