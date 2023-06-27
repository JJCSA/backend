package com.jjcsa.controller;

import com.jjcsa.dto.ContactUsRequest;
import com.jjcsa.service.CaptchaService;
import com.jjcsa.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

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
        boolean isCaptchaValid = captchaService.validateCaptcha(contactUsRequest.captchaToken(), clientIp);
        log.info("isCaptchaValid {}",isCaptchaValid);

        if (isCaptchaValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
