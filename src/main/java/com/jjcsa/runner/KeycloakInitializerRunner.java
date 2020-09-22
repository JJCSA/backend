package com.jjcsa.runner;

import com.jjcsa.util.KeycloakUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    private final Keycloak keycloakAdmin;

    /**
     * This method will initialize the Keycloak environment.
     * It initial will create relam, client, users, roles and credentials
     */
    @Override
    public void run(String... args) throws Exception {


        final Optional<RealmRepresentation> representationOptional = keycloakAdmin.realms().findAll().stream()
                .filter(r -> r.getRealm().equals(KeycloakUtil.JJCSA_REALM_NAME)).findAny();
        //If realm is not created, create the realm along with client, users, roles
        if (representationOptional.isPresent()) {
            log.info("'{}' realm is already pre-configured", KeycloakUtil.JJCSA_REALM_NAME);
        } else {
            log.info("Initializing '{}' realm in Keycloak ...", KeycloakUtil.JJCSA_REALM_NAME);
            final RealmRepresentation realmRepresentation = new RealmRepresentation();
            realmRepresentation.setRealm(KeycloakUtil.JJCSA_REALM_NAME);
            realmRepresentation.setEnabled(true);
            realmRepresentation.setRegistrationAllowed(true);

            // Client
            final ClientRepresentation clientRepresentation = new ClientRepresentation();
            clientRepresentation.setClientId(KeycloakUtil.JJCSA_CLIENT_ID);
            clientRepresentation.setDirectAccessGrantsEnabled(true);
            clientRepresentation.setDefaultRoles(new String[]{KeycloakUtil.JJCSA_ROLES.get(1)});
            clientRepresentation.setPublicClient(true);
            clientRepresentation.setRedirectUris(Collections.singletonList(KeycloakUtil.JJCSA_REDIRECT_URL));
            realmRepresentation.setClients(Collections.singletonList(clientRepresentation));

            // Users
            final List<UserRepresentation> userRepresentations = KeycloakUtil.JJCSA_USERS.stream().map(userPass -> {
                // Client roles
                final Map<String, List<String>> clientRoles = new HashMap<>();
                if ("admin".equals(userPass.getEmail())) {
                    clientRoles.put(KeycloakUtil.JJCSA_CLIENT_ID, KeycloakUtil.JJCSA_ROLES);
                } else {
                    clientRoles.put(KeycloakUtil.JJCSA_CLIENT_ID, Collections.singletonList(KeycloakUtil.JJCSA_ROLES.get(1)));
                }

                // User Credentials
                final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(userPass.getPassword());

                // User
                final UserRepresentation userRepresentation = new UserRepresentation();
                userRepresentation.setUsername(userPass.getEmail());
                userRepresentation.setEnabled(true);
                userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
                userRepresentation.setClientRoles(clientRoles);

                return userRepresentation;
            }).collect(Collectors.toList());
            realmRepresentation.setUsers(userRepresentations);

            // Create Realm
            keycloakAdmin.realms().create(realmRepresentation);
            log.info("'{}' realm created", KeycloakUtil.JJCSA_REALM_NAME);
        }
    }

}
