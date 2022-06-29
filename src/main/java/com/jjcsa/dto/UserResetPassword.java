package com.jjcsa.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserResetPassword {
    @NotNull(message = "Please enter user email")
    private String email;

    @NotNull(message = "Please enter temp password sent to your email")
    private String tempPassword;

    @NotNull(message = "Please enter your new password")
    private String newPassword;
}
