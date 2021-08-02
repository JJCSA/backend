package com.jjcsa.util;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.core.Response;
import java.util.*;

import static java.util.Objects.isNull;

@Slf4j
@Service
@Data
public class KeycloakUtil {

    public static String keycloakServerUrl;

    @Value("${keycloak.auth-server-url:http://localhost:8080/auth}")
    public void setKeycloakServerUrl(String keycloakServerUrl) {
        KeycloakUtil.keycloakServerUrl = keycloakServerUrl;
    }

    public static final String JJCSA_REALM_NAME = "jjcsa-services";
    public static final String JJCSA_CLIENT_ID = "jjcsa";
    private static final String JJCSA = "jjcsa";
    public static final String JJCSA_REDIRECT_URL = "http://localhost:3000/*";
    public static final Map<String,String> emailToPwMap = new HashMap<String,String>() {{
        put("admin","admin");
        put("user","user");
    }};

    private static RealmResource keycloakRealmResource;
    private static ClientRepresentation clientRepresentation;

    private static RealmResource getRealmResource() {
        if(isNull(keycloakRealmResource)) {
            Keycloak keycloakClient = KeycloakBuilder.builder()
                    .serverUrl(KeycloakUtil.keycloakServerUrl)
                    .realm(JJCSA_REALM_NAME)
                    .username("admin")
                    .password("admin")
                    .clientId(JJCSA_CLIENT_ID)
                    .build();
            keycloakRealmResource = keycloakClient.realm(KeycloakUtil.JJCSA_REALM_NAME);

            if(isNull(keycloakRealmResource)) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak realm not found");
            }
        }

        return keycloakRealmResource;
    }

    private static ClientRepresentation getClientRepresentation() {
        if(isNull(clientRepresentation)) {
            clientRepresentation = getRealmResource().clients().findByClientId(KeycloakUtil.JJCSA_CLIENT_ID).get(0);

            if(isNull(clientRepresentation)) {
                throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak client not found");
            }
        }

        return clientRepresentation;
    }

    public static boolean deleteUser(User user){
        UsersResource usersResource = getRealmResource().users();
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

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmail(addNewUser.getEmail());
        user.setUsername(addNewUser.getEmail());
        user.setFirstName(addNewUser.getFirstName());
        user.setLastName(addNewUser.getLastName());

        UsersResource usersResource = getRealmResource().users();

        // Create User
        Response response = usersResource.create(user);
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

    public static void addUserRole(String email, UserRole role) {

        // get the RoleRepresentation
        RoleRepresentation roleRepresentation = getRealmResource().clients().get(clientRepresentation.getId()).roles().get(role.getRoleText()).toRepresentation();

        // get the UsersResource
        UsersResource usersResource = getRealmResource().users();

        // get userId
        String userId = usersResource.search(email, true).get(0).getId();

        // add role for the user
        usersResource.get(userId)
                .roles()
                .clientLevel(getClientRepresentation().getId())
                .add(Arrays.asList(roleRepresentation));

    }

    public static void removeUserRole(String email, UserRole role) {

        // get the RoleRepresentation
        RoleRepresentation roleRepresentation = getRealmResource().clients().get(clientRepresentation.getId()).roles().get(role.getRoleText()).toRepresentation();

        // get the UsersResource
        UsersResource usersResource = getRealmResource().users();

        // get userId
        String userId = usersResource.search(email, true).get(0).getId();

//        RoleScopeResource userClientRoles
//                = usersResource.get(userId)
//                    .roles()
//                    .clientLevel(getClientRepresentation().getId());
//
//        // validate if user has role
//        userClientRoles.

        // remove role for the user
        usersResource.get(userId)
                .roles()
                .clientLevel(getClientRepresentation().getId())
                .remove(Arrays.asList(roleRepresentation));
    }
}
