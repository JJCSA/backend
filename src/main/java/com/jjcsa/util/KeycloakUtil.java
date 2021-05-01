package com.jjcsa.util;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.*;

@Slf4j
public class KeycloakUtil {

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String JJCSA_REALM_NAME = "jjcsa-services";
    public static final String JJCSA_CLIENT_ID = "jjcsa";
    public static final List<String> JJCSA_ROLES = Arrays.asList(ADMIN, USER);
    public static final String JJCSA_REDIRECT_URL = "http://localhost:3000/*";
    public static final List<User> JJCSA_USERS = Arrays.asList(new User("admin", "admin"),
            new User("user", "user"));

    public static boolean createNewUser(AddNewUser addNewUser, String keycloakServerUrl) {

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(addNewUser.getEmail());
        user.setUsername(addNewUser.getEmail());
        user.setFirstName(addNewUser.getFirstName());
        user.setLastName(addNewUser.getLastName());

        // Get realm
        RealmResource realmResource = getKeycloakClient(keycloakServerUrl).realm(JJCSA_REALM_NAME);
        UsersResource usersResource = realmResource.users();

        System.out.println(addNewUser);

        // Create User
        Response response = usersResource.create(user);
        System.out.println(response);
        if (response.getStatus() != HttpStatus.SC_CREATED) {
            if (response.getStatus() == HttpStatus.SC_CONFLICT) {
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

    public static boolean isAdmin(SimpleKeycloakAccount account) {
        return account.getRoles().contains(UserRole.Admin.name());
    }

    public static void updateUserRole(@NonNull String role,
                               @NonNull String email,
                               @NonNull String action,
                               @NonNull String serverUrl) {

        // Get realm
        RealmResource realmResource = getKeycloakClient(serverUrl).realm(KeycloakUtil.JJCSA_REALM_NAME);
        UsersResource usersResource = realmResource.users();

        // Client Representation for realm-management
        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(KeycloakUtil.JJCSA_CLIENT_ID).get(0);

        // Role Representation for manage-users
        RoleRepresentation roleRepresentation = realmResource.clients().get(clientRepresentation.getId()).roles().get(role).toRepresentation();

        // Admin userId
        String userId = usersResource.search(email, true).get(0).getId();

        // Update user with clientRole
        if(action.toLowerCase().contains("add")) {
            usersResource.get(userId).roles().clientLevel(clientRepresentation.getId()).add(Arrays.asList(roleRepresentation));
        } else {
            usersResource.get(userId).roles().clientLevel(clientRepresentation.getId()).remove(Arrays.asList(roleRepresentation));
        }

    }

    private static Keycloak getKeycloakClient(String keycloakServerUrl) {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(JJCSA_REALM_NAME)
                .username("admin")
                .password("admin")
                .clientId(JJCSA_CLIENT_ID)
                .build();
    }
}
