package com.jjcsa.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ContactUsRequest {

    @NotBlank(message = "token must not be null!")
    private String captchaToken;

    @NotBlank(message = "please enter your name")
    private String name;

    @NotBlank(message = "please enter a valid email")
    private String email;

    @NotBlank(message = "please enter a valid message")
    private String message;

}
