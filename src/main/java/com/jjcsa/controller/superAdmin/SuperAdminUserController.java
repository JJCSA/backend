package com.jjcsa.controller.superAdmin;

import com.jjcsa.dto.UpdateUserRole;
import com.jjcsa.model.AdminAction;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.Action;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.repository.AdminActionRepository;
import com.jjcsa.service.KeycloakService;
import com.jjcsa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping(path = "/api/super-admin/user", produces = "application/json")
@RequiredArgsConstructor
public class SuperAdminUserController {

    private final UserService userService;
    private final KeycloakService keycloakService;
    private final AdminActionRepository adminActionRepository;

    @PostMapping(path = "{userId}/role")
    public ResponseEntity addUserRole(@PathVariable String userId, @RequestBody UpdateUserRole updateUserRole, KeycloakAuthenticationToken authenticationToken) {

        User user = userService.getUserById(userId);
        if(isNull(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id not found");

        AdminAction adminAction = initAdminAction(authenticationToken, userId);

        keycloakService.addUserRole(user.getId(), updateUserRole.getRole());

        if(updateUserRole.getRole().equals(UserRole.ADMIN)) {
            adminAction.setAction(Action.PROMOTE_USER_TO_ADMIN);
            adminAction.setDescrip(String.format("User %s promoted to admin by super-admin", user.getEmail()));
            adminActionRepository.save(adminAction);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "{userId}/role/{role}")
    public ResponseEntity removeUserRole(@PathVariable String userId, @PathVariable UserRole role, KeycloakAuthenticationToken authenticationToken) {

        User user = userService.getUserById(userId);
        if(isNull(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id not found");

        AdminAction adminAction = initAdminAction(authenticationToken, userId);

        keycloakService.removeUserRole(user.getId(), role);

        if(role.equals(UserRole.ADMIN)) {
            adminAction.setAction(Action.DEMOTE_ADMIN_TO_USER);
            adminAction.setDescrip(String.format("User %s removed as admin by super-admin", user.getEmail()));
            adminActionRepository.save(adminAction);
        }

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private AdminAction initAdminAction(KeycloakAuthenticationToken authenticationToken,
                                        String userId) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        User supAdminUser = userService.getUserById(token.getSubject());

        AdminAction adminAction = new AdminAction();
        adminAction.setFromUserId(supAdminUser.getId());
        adminAction.setToUserId(userId);
        adminAction.setDateOfAction(new Date());

        return adminAction;

    }
}
