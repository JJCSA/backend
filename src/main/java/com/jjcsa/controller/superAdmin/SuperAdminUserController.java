package com.jjcsa.controller.superAdmin;

import com.jjcsa.dto.UpdateUserRole;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.service.UserService;
import com.jjcsa.util.KeycloakUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping(path = "/api/super-admin/user", produces = "application/json")
@RequiredArgsConstructor
public class SuperAdminUserController {

    private final UserService userService;

    @PostMapping(path = "{userId}/role")
    public ResponseEntity addUserRole(@PathVariable UUID userId, @RequestBody UpdateUserRole updateUserRole) {
        // TODO: check if default validation works for invalid role

        User user = userService.getUserById(userId);
        if(isNull(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id not found");

        KeycloakUtil.addUserRole(user.getEmail(), updateUserRole.role);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "{userId}/role/{role}")
    public ResponseEntity addUserRole(@PathVariable UUID userId, @PathVariable UserRole role) {
        // TODO: check if default validation works for invalid role

        User user = userService.getUserById(userId);
        if(isNull(user)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User id not found");

        KeycloakUtil.removeUserRole(user.getEmail(), role);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
