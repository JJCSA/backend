package com.jjcsa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.util.RecaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaptchaService {

    @Value("${google.recaptcha.secret:}")
    private String reCaptchaSecret;

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
}