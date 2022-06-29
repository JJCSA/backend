package com.jjcsa.service;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.util.KeycloakConstants;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class KeycloakService {

    @Value("${keycloak.auth-server-url:http://localhost:8080/auth}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.username:admin}")
    private String keycloakUsername;

    @Value("${keycloak.admin.password:admin}")
    private String keycloakPassword;

    private RealmResource keycloakRealmResource;
    private ClientRepresentation clientRepresentation;
    private ClientResource clientResource;

    private RealmResource getKeycloakRealmResource() {
        if (isNull(keycloakRealmResource)) {
            Keycloak keycloakClient = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm(KeycloakConstants.KEYCLOAK_REALM_NAME)
                    .username(keycloakUsername)
                    .password(keycloakPassword)
                    .clientId(KeycloakConstants.KEYCLOAK_CLIENT)
                    .build();
            keycloakRealmResource = keycloakClient.realm(KeycloakConstants.KEYCLOAK_REALM_NAME);

            if (isNull(keycloakRealmResource)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak realm not found");
            }
        }

        return keycloakRealmResource;
    }

    private ClientRepresentation getClientRepresentation() {
        if (isNull(clientRepresentation)) {
            clientRepresentation = getKeycloakRealmResource().clients().findByClientId(KeycloakConstants.KEYCLOAK_CLIENT).get(0);

            if (isNull(clientRepresentation)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak client not found");
            }
        }

        return clientRepresentation;
    }

    private ClientResource getClientResource() {
        if (isNull(clientResource)) {
            clientResource = getKeycloakRealmResource().clients().get(getClientRepresentation().getId());

            if (isNull(clientRepresentation)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak client not found");
            }
        }

        return clientResource;
    }

    public UserResource getUserResource(String userId) {
        // get the UsersResource
        UsersResource usersResource = getKeycloakRealmResource().users();

        return usersResource.get(userId);
    }

    public String createNewUser(AddNewUser addNewUser) {

        // Define user
        UserRepresentation user = new UserRepresentation();
        // new user is disabled by default, this will prevent them from signing in until approved
        user.setEnabled(false);
        user.setEmail(addNewUser.getEmail());
        user.setUsername(addNewUser.getEmail());

        UsersResource usersResource = getKeycloakRealmResource().users();

        // Create User
        Response response = usersResource.create(user);
        if (response.getStatus() != HttpStatus.CREATED.value()) {
            if (response.getStatus() == HttpStatus.CONFLICT.value()) {
                throw new BadRequestException("User already exists",
                        "User with this email address already exists",
                        "Please login",
                        "Please try logging in with this email address",
                        "");
            } else {
                log.error("Unable to create new user in Keycloak");
                return null;
            }
        }

        String userId = CreatedResponseUtil.getCreatedId(response);
        log.debug("New user created in Keycloak with userId {}", userId);

        // Define password credential
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(addNewUser.getPassword());

        UserResource userResource = usersResource.get(userId);

        // set password credential
        userResource.resetPassword(credentialRepresentation);

        return userId;
    }

    public boolean deleteUser(User user) {

        log.info("Deleting user from keycloak with email {}", user.getEmail());

        UsersResource usersResource = getKeycloakRealmResource().users();

        Response response = usersResource.delete(user.getId());
        if(response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }

        return true;
    }

    /**
     * Enables a user in Keycloak.
     * New user is disabled in keycloak by default.
     * Once approved by an admin, user should be enabled in keycloak
     *
     * @param userId User's id
     */
    public void enableUser(String userId) {
        log.debug("Enabling user with id {} in keycloak", userId);

        // Get UserResource
        UserResource userResource = getUserResource(userId);

        UserRepresentation userRepresentation = null;
        try {
            // Get UserRepresentation
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            log.error("Could not find user {} in keycloak", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user in keycloak");
        }

        userRepresentation.setEnabled(true);

        // update
        userResource.update(userRepresentation);

        log.debug("Enabled user with id {} in keycloak successfully", userId);
    }

    public void addUserRole(String userId, UserRole role) {

        // get the RoleRepresentation
        RoleRepresentation roleRepresentation = getClientResource().roles().get(role.getRoleText()).toRepresentation();

        // Get UserResource
        UserResource userResource = getUserResource(userId);

        try {
            // add role for the user
            userResource.roles()
                    .clientLevel(getClientRepresentation().getId())
                    .add(Arrays.asList(roleRepresentation));

        } catch (NotFoundException e) {
            log.error("Could not find user {} in keycloak", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user in keycloak");
        }

    }

    public void removeUserRole(String userId, UserRole role) {

        // get the RoleRepresentation
        RoleRepresentation roleRepresentation = getClientResource().roles().get(role.getRoleText()).toRepresentation();

        // Get UserResource
        UserResource userResource = getUserResource(userId);

        try {
            // remove role for the user
            userResource.roles()
                    .clientLevel(getClientRepresentation().getId())
                    .remove(Arrays.asList(roleRepresentation));

        } catch (NotFoundException e) {
            log.error("Could not find user {} in keycloak", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user in keycloak");
        }

    }

    public UserRole getUserRole(String userId) {
        // Get UserResource
        UserResource userResource = getUserResource(userId);

        List<String> clientRoles = null;

        try {
            // fetch client level roles for user
            clientRoles = userResource.roles()
                    .clientLevel(getClientRepresentation().getId())
                    .listEffective()
                    .stream()
                    .map(roleRepresentation -> roleRepresentation.getName())
                    .collect(Collectors.toList());

        } catch (NotFoundException e) {
            log.error("Could not find user {} in keycloak", userId);
            return null;
        }

        if(clientRoles.contains(UserRole.SUPER_ADMIN.toString())) {
            return UserRole.SUPER_ADMIN;
        }

        if(clientRoles.contains(UserRole.ADMIN.toString())) {
            return UserRole.ADMIN;
        }

        return UserRole.USER;
    }

    public Boolean resetUserPassword(String userId, String newPassword) {
        // Get UserResource
        UserResource userResource = getUserResource(userId);

        // Create password credential
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(newPassword);

        // set password credential
        userResource.resetPassword(credentialRepresentation);

        return true;
    }
}
