package com.jjcsa.util;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.User;
import lombok.Data;
import com.jjcsa.model.enumModel.UserRole;
import lombok.Data;
import com.jjcsa.model.enumModel.UserRole;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import com.jjcsa.model.enumModel.RoleAction;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import com.jjcsa.model.enumModel.RoleAction;
import lombok.Data;
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
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Data
public class KeycloakUtil {

    public static String keycloakServerUrl;

    @Value("${keycloak.auth-server-url:http://localhost:8080/auth}")
    public void setKeycloakServerUrl(String keycloakServerUrl) {
        KeycloakUtil.keycloakServerUrl = keycloakServerUrl;
    }

    public static final String ADMIN = "Admin";
    public static final String USER = "User";
    public static final String SUPER_ADMIN = "SuperAdmin";
    public static final String JJCSA_REALM_NAME = "jjcsa-services";
    public static final String JJCSA_CLIENT_ID = "jjcsa";
    public static final List<String> JJCSA_ROLES = Arrays.asList(ADMIN, USER, SUPER_ADMIN);
    private static final String JJCSA = "jjcsa";
    public static final String JJCSA_REDIRECT_URL = "http://localhost:3000/*";
    public static final Map<String,String> emailToPwMap = new HashMap<String,String>() {{
        put("admin","admin");
        put("user","user");
    }};

    private static Keycloak getKeyCloakClient(){
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(JJCSA_REALM_NAME)
                .username("admin")
                .password("admin")
                .clientId(JJCSA_CLIENT_ID)
                .build();
    }

    public static boolean deleteUser(User user){
        Keycloak keycloak = KeycloakUtil.getKeyCloakClient();
        UsersResource usersResource = keycloak.realm(JJCSA_REALM_NAME).users();
        Optional<UserRepresentation> keyCloakUser = usersResource.list().stream().filter(keyClockUser -> keyClockUser.getEmail().equalsIgnoreCase(user.getEmail())).findFirst();
        if(keyCloakUser.isPresent()) {
            Response response = usersResource.delete(keyCloakUser.get().getId());
            if(response.getStatus() == HttpStatus.SC_NO_CONTENT) {
                log.info("User Successfully deleted from keycloak");
                return true;
            }
        }
        return false;
    }

    public static boolean createNewUser(AddNewUser addNewUser) {
        Keycloak keycloak = KeycloakUtil.getKeyCloakClient();

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

    public static void updateUserRole(@NonNull String role,
                               @NonNull String email,
                               @NonNull String action) {

        // Get realm
        RealmResource realmResource = getKeyCloakClient().realm(KeycloakUtil.JJCSA_REALM_NAME);
        UsersResource usersResource = realmResource.users();

        // Client Representation for realm-management
        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(KeycloakUtil.JJCSA_CLIENT_ID).get(0);

        log.info("roles: {}", realmResource.clients().get(clientRepresentation.getId()).roles().get(role));
        
        // Role Representation for manage-users
        RoleRepresentation roleRepresentation = realmResource.clients().get(clientRepresentation.getId()).roles().get(role).toRepresentation();

        // Admin userId
        String userId = usersResource.search(email, true).get(0).getId();

        // Update user with clientRole
        if(action.equalsIgnoreCase(RoleAction.Add.name())) {
            usersResource.get(userId).roles().clientLevel(clientRepresentation.getId()).add(Arrays.asList(roleRepresentation));
        } else {
            usersResource.get(userId).roles().clientLevel(clientRepresentation.getId()).remove(Arrays.asList(roleRepresentation));
        }

    }
}
