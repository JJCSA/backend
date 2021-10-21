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

    public boolean createNewUser(AddNewUser addNewUser) {

        // Define user
        UserRepresentation user = new UserRepresentation();
        // new user is disabled by default, this will prevent them from signing in until approved
        user.setEnabled(false);
        user.setEmail(addNewUser.getEmail());
        user.setUsername(addNewUser.getEmail());
        user.setFirstName(addNewUser.getFirstName());
        user.setLastName(addNewUser.getLastName());

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
                return false;
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

        return true;
    }

    public boolean deleteUser(User user) {

        log.info("Deleting user from keycloak with email {}", user.getEmail());

        UsersResource usersResource = getKeycloakRealmResource().users();
        List<UserRepresentation> userRepresentationList = usersResource.search(user.getEmail());
        if (isNull(userRepresentationList) || userRepresentationList.size() == 0) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "User not found");
        }

        UserRepresentation userRepresentation = userRepresentationList.get(0);
        usersResource.delete(userRepresentation.getId());

        return true;
    }

    /**
     * Enables a user in Keycloak.
     * New user is disabled in keycloak by default.
     * Once approved by an admin, user should be enabled in keycloak
     *
     * @param email User's email address
     */
    public void enableUser(String email) {
        log.debug("Enabling user with email {} in keycloak", email);

        // get the UsersResource
        UsersResource usersResource = getKeycloakRealmResource().users();

        // get userId
        String userId = usersResource.search(email, true).get(0).getId();

        // Get UserRepresentation
        UserRepresentation userRepresentation = usersResource.get(userId).toRepresentation();
        userRepresentation.setEnabled(true);

        // update
        usersResource.get(userId).update(userRepresentation);

        log.debug("Enabled user with email {} in keycloak successfully", email);
    }

    public void addUserRole(String email, UserRole role) {

        // get the RoleRepresentation
        RoleRepresentation roleRepresentation = getClientResource().roles().get(role.getRoleText()).toRepresentation();

        // get the UsersResource
        UsersResource usersResource = getKeycloakRealmResource().users();

        // get userId
        String userId = usersResource.search(email, true).get(0).getId();

        // add role for the user
        usersResource.get(userId)
                .roles()
                .clientLevel(getClientRepresentation().getId())
                .add(Arrays.asList(roleRepresentation));

    }

    public void removeUserRole(String email, UserRole role) {

        // get the RoleRepresentation
        RoleRepresentation roleRepresentation = getClientResource().roles().get(role.getRoleText()).toRepresentation();

        // get the UsersResource
        UsersResource usersResource = getKeycloakRealmResource().users();

        // get userId
        String userId = usersResource.search(email, true).get(0).getId();

        // remove role for the user
        usersResource.get(userId)
                .roles()
                .clientLevel(getClientRepresentation().getId())
                .remove(Arrays.asList(roleRepresentation));
    }

    public UserRole getUserRole(String email) {
        // get the UsersResource
        UsersResource usersResource = getKeycloakRealmResource().users();

        // get userId
        String userId = usersResource.search(email, true).get(0).getId();

        List<String> clientRoles = usersResource.get(userId)
                .roles()
                .clientLevel(getClientRepresentation().getId())
                .listEffective()
                .stream()
                .map(roleRepresentation -> roleRepresentation.getName())
                .collect(Collectors.toList());

        if(clientRoles.contains(UserRole.SUPER_ADMIN.toString())) {
            return UserRole.SUPER_ADMIN;
        }

        if(clientRoles.contains(UserRole.ADMIN.toString())) {
            return UserRole.ADMIN;
        }

        return UserRole.USER;
    }
}
