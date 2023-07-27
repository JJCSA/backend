package com.jjcsa.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;

public class StringUtil {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    // https://stackoverflow.com/a/157202
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String generateForgotPasswordLink(String forgotPasswordURL, String email, String tempPassword) {
        return
                UriComponentsBuilder
                        .fromUriString(forgotPasswordURL)
                        .queryParam("email", email)
                        .queryParam("tempPassword", tempPassword)
                        .build()
                        .toUriString();
    }
}
