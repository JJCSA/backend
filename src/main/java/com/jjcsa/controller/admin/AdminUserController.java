package com.jjcsa.controller.admin;

import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public String deleteUser(@PathVariable UUID userId){

        log.info("Delete User invoked for userId:{}", userId);
        User user = userService.getUserById(userId);
        userService.deleteUser(user);
        return "User successfully deleted";
    }

    @GetMapping(path = "")
    public List<User> getUsersList() {
        log.info("Getting User List");
        return userService.getAllUsers();
    }

    @PutMapping(path = "/status")
    public boolean updateUserStatus(@RequestParam UUID userId, @RequestParam UserStatus status) {

        log.info("Find User for userId: {}", userId);
        User user = userService.getUserById(userId);

        if(isNull(user)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find user");
        }

        return userService.updateUserStatus(user, status);
    }
}
