package com.jjcsa.controller;

import com.jjcsa.model.User;
import com.jjcsa.service.UserService;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Data
@RequestMapping(path="/api/admin", produces = "application/json")
public class AdminController {

    @NonNull
    private UserService userService;

    /**
     * Delete user method to delete the user with {userId} provided as param.
     * @param userId user to be deleted. It will be email id of the user.
     * @returns the message on successful deletion of user
     */
    @PostMapping(path = "delete/{userId}")
    public String deleteUser(@PathVariable String userId){

        log.info("Delete User invoked for userId:{}", userId);
        User user = userService.getUser(userId);
        userService.deleteUser(user);
        return "User successfully deleted";
    }
}
