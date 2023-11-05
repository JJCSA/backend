package com.jjcsa.service;

import com.jjcsa.dto.RecaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.regex.Pattern;

// created from https://www.baeldung.com/spring-security-registration-captcha
@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaService {

    @Value("${google.recaptcha.secret:}")
    private String reCaptchaSecret;

    private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    public Boolean validateCaptcha(String token, String clientIp) {
        if(!responseSanityCheck(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Response contains invalid characters");
        }

        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                reCaptchaSecret, token, clientIp));

        RestTemplate restTemplate = new RestTemplate();
        RecaptchaResponse googleResponse = restTemplate.getForObject(verifyUri, RecaptchaResponse.class);

        if (googleResponse == null || !googleResponse.isSuccess()) {
            return false;
        }
        return true;
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }
}