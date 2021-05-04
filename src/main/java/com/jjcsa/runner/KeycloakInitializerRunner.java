package com.jjcsa.runner;

import com.jjcsa.util.KeycloakUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeycloakInitializerRunner implements CommandLineRunner {

    @Value("${keycloak.auth-server-url:http://localhost:8080/auth}")
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
            final List<UserRepresentation> userRepresentations = KeycloakUtil.emailToPwMap.keySet().stream().map(userPass -> {
                // Client roles
                final Map<String, List<String>> clientRoles = new HashMap<>();
                if ("admin".equals(userPass)) {
                    clientRoles.put(KeycloakUtil.JJCSA_CLIENT_ID, KeycloakUtil.JJCSA_ROLES);
                } else {
                    clientRoles.put(KeycloakUtil.JJCSA_CLIENT_ID, Collections.singletonList(KeycloakUtil.JJCSA_ROLES.get(1)));
                }

                // User Credentials
                final CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
                credentialRepresentation.setValue(KeycloakUtil.emailToPwMap.get(userPass));

                // User
                final UserRepresentation userRepresentation = new UserRepresentation();
                userRepresentation.setUsername(userPass);
                userRepresentation.setEnabled(true);
                userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
                userRepresentation.setClientRoles(clientRoles);

                return userRepresentation;
            }).collect(Collectors.toList());
            realmRepresentation.setUsers(userRepresentations);

            // Create Realm
            keycloakAdmin.realms().create(realmRepresentation);

            // Update Admin role to add "manage-users" role
            try {
                updateAdminRole();
            } catch (Exception ex) {
                log.error("Unable to change clientRole for admin user");
            }

            log.info("'{}' realm created", KeycloakUtil.JJCSA_REALM_NAME);
        }
    }

    public void updateAdminRole() {

        // Get realm
        RealmResource realmResource = keycloakAdmin.realm(KeycloakUtil.JJCSA_REALM_NAME);
        UsersResource usersResource = realmResource.users();

        // Client Representation for realm-management
        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId("realm-management").get(0);

        // Role Representation for manage-users
        RoleRepresentation roleRepresentation = realmResource.clients().get(clientRepresentation.getId()).roles().get("manage-users").toRepresentation();

        // Admin userId
        String userId = usersResource.search("admin", true).get(0).getId();

        // Update user with clientRole
        usersResource.get(userId).roles().clientLevel(clientRepresentation.getId()).add(Arrays.asList(roleRepresentation));

    }

}
