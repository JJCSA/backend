package com.jjcsa.controller.admin;

import com.jjcsa.dto.UpdateUserStatusDto;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.model.AdminAction;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.service.UserService;
import com.jjcsa.util.CsvGeneratorUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
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

    @GetMapping(path = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> getUsersListInCsv() {
        List<UserDTO> allUsers = userService.getAllUsers();
        byte[] csvBytes = CsvGeneratorUtil.generateCSV(allUsers).getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "user.csv");

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    @GetMapping(path="/{userId}/communityProof")
    public String getCommunityProof(@PathVariable String userId){
        log.info("Getting community proof for userId");
        return userService.getCommunityProof(userId);
    }
    @PutMapping(path = "/status")
    public boolean updateUserStatus(@RequestBody @Valid UpdateUserStatusDto updateUserStatusDto, KeycloakAuthenticationToken authenticationToken) {

        log.info("Find User for userId: {}", updateUserStatusDto.getUserId());
        User user = userService.getUserById(updateUserStatusDto.getUserId());

        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find user");
        }

        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User adminUser = userService.getUserById(token.getSubject());
        if (isNull(adminUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find admin user making the request");
        }


        return userService.updateUserStatus(user, adminUser, updateUserStatusDto);
    }

    @PostMapping(path = "/{userId}/regional-contact")
    public Boolean updateUserRegionalContact(@PathVariable String userId,
                                             @RequestParam Boolean isRegionalContact,
                                             KeycloakAuthenticationToken authToken) {

        log.info("Find User for userId: {}", userId);
        User user = userService.getUserById(userId);

        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find user");
        }

        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User adminUser = userService.getUserById(token.getSubject());
        AdminAction adminAction = new AdminAction();
        adminAction.setFromUserId(adminUser.getId());
        adminAction.setToUserId(userId);
        adminAction.setDateOfAction(new Date());

        return userService.updateUserRegionalContact(user, isRegionalContact, adminAction);
    }
}
