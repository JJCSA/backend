package com.jjcsa.controller;

import com.jjcsa.dto.ContactUsRequest;
import com.jjcsa.service.CaptchaService;
import com.jjcsa.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

// for testing recapta, try: https://codesandbox.io/s/recaptcha-v3-generate-response-token-forked-qmmd95?file=/src/index.js:1013-1053

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/contactus", produces = "application/json")
public class ContactUsController {

    private final CaptchaService captchaService;

    @PostMapping("/verify")
    public ResponseEntity verify(@RequestBody @Valid ContactUsRequest contactUsRequest, HttpServletRequest request) {
        log.info("Verifying user captcha for contact us");

        String clientIp = GeneralUtil.getClientIp(request);
        boolean isCaptchaValid = captchaService.validateCaptcha(contactUsRequest.getCaptchaToken(), clientIp);
        log.info("isCaptchaValid {}",isCaptchaValid);

        if (!isCaptchaValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recaptcha token is invalid");
        }

        return ResponseEntity.ok().build();
    }
}
