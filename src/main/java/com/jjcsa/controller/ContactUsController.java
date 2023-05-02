package com.jjcsa.controller;

import com.jjcsa.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/contactus", produces = "application/json")
public class ContactUsController {

    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody Map<String, String> requestBody) throws IOException {
        log.info("Inside Verify");
        String recaptchaToken = requestBody.get("captchaToken");
        if (recaptchaToken == null || recaptchaToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Response token is missing");
        }
        boolean isCaptchaValid = captchaService.verifyCaptcha(recaptchaToken);
        log.info("isValid {}",isCaptchaValid);

        if (isCaptchaValid) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
