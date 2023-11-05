package com.jjcsa.controller;

import com.jjcsa.dto.UserResetPassword;
import com.jjcsa.service.UserForgotPasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/user-password", produces = "application/json")
public class UserForgotPasswordController {

    private final UserForgotPasswordService userForgotPasswordService;

    @PostMapping(path = "/forgot-password")
    public Boolean generateTempPassword(@RequestParam String email) {
        return userForgotPasswordService.generateTempPasswordForEmail(email);
    }

    @PostMapping(path = "/reset-password")
    public Boolean resetUserPassword(@RequestBody @Valid UserResetPassword userResetPassword) {
        return userForgotPasswordService.changeUserPasswordForTempPassword(userResetPassword);
    }
}
