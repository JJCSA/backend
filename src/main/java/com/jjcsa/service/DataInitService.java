package com.jjcsa.service;

import com.jjcsa.dto.AddNewUser;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

// Service class that initiates seed data
// This is created only to support local development

@Profile({"local"}) // local profile only
@Service
@Slf4j
@RequiredArgsConstructor
public class DataInitService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final UserMapper userMapper;

    @Transactional
    public Boolean initData() throws IOException {

        // delete all data in database
        List<User> allUsers = userRepository.findAll();
        userRepository.deleteAll(allUsers);

        // delete all keycloak users
        if (!keycloakService.deleteAllUsers()) {
            log.error("Unable to delete all keycloak users");
            return false;
        }

        // Create users

        // Create Super Admin
        User superAdminUser = createUser("super-admin@gmail.com", "Super", "Admin");
        keycloakService.addUserRole(superAdminUser.getId(), UserRole.SUPER_ADMIN);

        User adminUser = createUser("admin@gmail.com", "Admin", "User");
        keycloakService.addUserRole(adminUser.getId(), UserRole.ADMIN);

        createUser("user1@gmail.com", "First1", "Last1");
        createUser("user2@gmail.com", "First2", "Last2");
        createUser("user3@gmail.com", "First3", "Last3");
        createUser("user4@gmail.com", "First4", "Last4");
        createUser("user5@gmail.com", "First5", "Last5");

        return true;
    }

    private User createUser(String email, String firstName, String lastName) {
        AddNewUser addNewUser = AddNewUser.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password("Test@123")
                .mobileNumber("1234567890")
                .prefMethodOfContact("Email")
                .jainCommunity("xyz")
                .build();

        String userId = keycloakService.createNewUser(addNewUser);
        keycloakService.enableUser(userId);

        User user = userMapper.toUserProfile(addNewUser);

        // Set defaults
        user.setId(userId);
        user.setUserStatus(UserStatus.Active);

        return userRepository.save(user);
    }

}
