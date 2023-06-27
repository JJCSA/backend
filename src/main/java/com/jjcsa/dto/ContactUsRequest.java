package com.jjcsa.dto;

import javax.validation.constraints.NotBlank;

public record ContactUsRequest(@NotBlank(message = "token must not be null!") String captchaToken) {
}
