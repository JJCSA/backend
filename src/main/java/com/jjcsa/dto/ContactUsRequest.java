package com.jjcsa.dto;

import javax.validation.constraints.NotBlank;

public record ContactUsRequest(@NotBlank(message = "token must not be null!") String captchaToken,
                               @NotBlank(message = "please enter your name") String name,
                               @NotBlank(message = "please enter a valid email") String email,
                               @NotBlank(message = "please enter a valid message") String message) {
}
