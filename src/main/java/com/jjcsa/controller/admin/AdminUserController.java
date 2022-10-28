package com.jjcsa.controller.admin;

import com.jjcsa.dto.UpdateUserStatusDto;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.model.AdminAction;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin/users", produces = "application/json")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * Delete user method to delete the user with {userId} provided as param.
     * @param userId user to be deleted.
     * @returns the message on successful deletion of user
     */
    @DeleteMapping(path = "/{userId}")
    public String deleteUser(@PathVariable String userId){

        log.info("Delete User invoked for userId:{}", userId);
        User user = userService.getUserById(userId);
        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found");
        }
        userService.deleteUser(user);
        return "User successfully deleted";
    }

    @GetMapping(path = "")
    public List<UserDTO> getUsersList() {
        log.info("Getting User List");
        return userService.getAllUsers();
    }

    @GetMapping(path="/{userId}/communityProof")
    public String getCommunityProof(@PathVariable String userId){
        log.info("Getting community proof for userId");
        return userService.getCommunityProof(userId);
    }
    @PutMapping(path = "/status")
    public boolean updateUserStatus(@RequestBody UpdateUserStatusDto updateUserStatusDto, KeycloakAuthenticationToken authenticationToken) {

        log.info("Find User for userId: {}", updateUserStatusDto.getUserId());
        User user = userService.getUserById(updateUserStatusDto.getUserId());

        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find user");
        }

        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User adminUser = userService.getUserById(token.getSubject());
        AdminAction adminAction = new AdminAction();
        adminAction.setFromUserId(adminUser.getId());
        adminAction.setToUserId(updateUserStatusDto.getUserId());
        adminAction.setDateOfAction(new Date());

        return userService.updateUserStatus(user, updateUserStatusDto.getStatus(), adminAction);
    }
}
