package com.jjcsa.runner;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.auth-server-url:http://localhost:8080/auth}")
    private String keycloakServerUrl;

    // TODO: Figure out a way to get keycloak credentials from properties
    /**
     * This is the admin console credentials for Keycloak server with username as admin
     * and password as password
     */
    @Bean
    Keycloak keycloakAdmin() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .username("admin")
                .password("password")
                .clientId("admin-cli")
                .build();
    }

}

