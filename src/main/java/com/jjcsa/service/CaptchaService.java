package com.jjcsa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.dto.RecaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

// created from https://www.baeldung.com/spring-security-registration-captcha
@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaService {

    @Value("${google.recaptcha.secret:}")
    private String reCaptchaSecret;

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    public boolean verifyCaptcha(String recaptchaToken) throws IOException {
        log.info("Inside verifyCaptcha");
        String urlParameters = "secret=" + URLEncoder.encode(reCaptchaSecret, "UTF-8") +
                "&response=" + URLEncoder.encode(recaptchaToken, "UTF-8");

        URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        conn.getOutputStream().write(urlParameters.getBytes("UTF-8"));

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder responseBuilder = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            responseBuilder.append(inputLine);
        }
        in.close();

        String responseString = responseBuilder.toString();
        log.info("Inside response {}",responseString);
        ObjectMapper objectMapper = new ObjectMapper();
        RecaptchaResponse recaptchaResponse = objectMapper.readValue(responseString, RecaptchaResponse.class);

        return recaptchaResponse.isSuccess();
    }

    public Boolean validateCaptcha(String token, String clientIp) {
        if(!responseSanityCheck(token)) {
            throw new BadRequestException("Response contains invalid characters", "", "", "", "");
        }

        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                reCaptchaSecret, URLEncoder.encode(token, StandardCharsets.UTF_8), clientIp));

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