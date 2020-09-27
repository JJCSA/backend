package com.jjcsa.util;

import java.util.Arrays;
import java.util.List;

import com.jjcsa.model.UserLogin;

public class KeycloakUtil {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String JJCSA_REALM_NAME = "jjcsa-services";
    public static final String JJCSA_CLIENT_ID = "jjcsa";
    public static final List<String> JJCSA_ROLES = Arrays.asList(ADMIN, USER);
    public static final String JJCSA_REDIRECT_URL = "http://localhost:3000/*";
    public static final List<UserLogin> JJCSA_USERS = Arrays.asList(new UserLogin("admin", "admin"),
            new UserLogin("user", "user"));
}
