package com.jjcsa.controller.admin;

import com.jjcsa.model.User;
import com.jjcsa.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin/users", produces = "application/json")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * Delete user method to delete the user with {userId} provided as param.
     * @param userId user to be deleted. It will be email id of the user.
     * @returns the message on successful deletion of user
     */
    @DeleteMapping(path = "/{userId}")
    public String deleteUser(@PathVariable String userId){

        log.info("Delete User invoked for userId:{}", userId);
        User user = userService.getUser(userId);
        userService.deleteUser(user);
        return "User successfully deleted";
    }

    @GetMapping(path = "")
    public List<User> getUsersList() {
        log.info("Getting User List");
        return userService.getAllUsers();
    }
}
