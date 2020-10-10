package com.jjcsa.util;

import java.util.Arrays;
import java.util.List;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.UserAlreadyExistsException;
import com.jjcsa.model.UserLogin;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.core.Response;

@Slf4j
public class KeycloakUtil {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String JJCSA_REALM_NAME = "jjcsa-services";
    public static final String JJCSA_CLIENT_ID = "jjcsa";
    public static final List<String> JJCSA_ROLES = Arrays.asList(ADMIN, USER);
    public static final String JJCSA_REDIRECT_URL = "http://localhost:3000/*";
    public static final List<UserLogin> JJCSA_USERS = Arrays.asList(new UserLogin("admin", "admin"),
            new UserLogin("user", "user"));

    public static boolean createNewUser(AddNewUser addNewUser, String keycloakServerUrl) {
        Keycloak keycloak = KeycloakBuilder.builder()
                                .serverUrl(keycloakServerUrl)
                                .realm(JJCSA_REALM_NAME)
                                .username("admin")
                                .password("admin")
                                .clientId(JJCSA_CLIENT_ID)
                                .build();

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(addNewUser.getEmail());
        user.setUsername(addNewUser.getEmail());
        user.setFirstName(addNewUser.getFirstName());
        user.setLastName(addNewUser.getLastName());

        // Get realm
        RealmResource realmResource = keycloak.realm(JJCSA_REALM_NAME);
        UsersResource usersResource = realmResource.users();

        // Create User
        Response response = usersResource.create(user);
        if(response.getStatus() != HttpStatus.SC_CREATED) {
            if(response.getStatus() == HttpStatus.SC_CONFLICT) {
                throw new UserAlreadyExistsException("User with this email address already exists!");
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

}
