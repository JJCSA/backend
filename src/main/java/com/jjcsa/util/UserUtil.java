package com.jjcsa.util;

import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjcsa.dto.UserProfile;
import com.jjcsa.mapper.UserMapper;
import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import com.jjcsa.service.UserService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;

@Slf4j
public class UserUtil {

    public static boolean isUserApproved(User user) {
        return user.getUserStatus().equals(UserStatus.Active);
    }

    public static boolean isUserPending(User user) {
        return user.getUserStatus().equals(UserStatus.Pending);
    }

    public static void copyNonNullProperties(Object destination,
                                       Object sourceObject) {
        try {
            PropertyUtils.describe(sourceObject).entrySet().stream()
            .filter(source -> source.getValue() != null)
            .filter(source -> !source.getKey().equals("class"))
            .forEach(source -> {
                try {
                    PropertyUtils.setProperty(destination, source.getKey(), source.getValue());
                } catch (Exception e22) {
                    log.error("Error setting properties : {}", e22.getMessage());
                }
            });
        } catch (Exception e1) {
            log.error("Error setting properties : {}", e1.getMessage());
        }

    }

    public static boolean isValidRole(String updatedRole) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equals(updatedRole)) {
                return true;
            }
        }

        return false;
    }

    public static User getUserDetailsToBeUpdated(@NonNull final String userProfileJSONString,
                                                 @NonNull final UserMapper userMapper,
                                                 @NonNull final UserService userService,
                                                 @NonNull final ObjectMapper objectMapper) throws JsonProcessingException {


        UserProfile updatedUserProfile = objectMapper.readValue(userProfileJSONString, UserProfile.class);
        log.info("Updating user with email {}", updatedUserProfile.getEmail());
        User userDetailsToBeUpdated = userMapper.toUser(updatedUserProfile);
        User existingUserDetails = userService.getUser(userDetailsToBeUpdated.getEmail());

        UserUtil.copyNonNullProperties(existingUserDetails, userDetailsToBeUpdated);

        return existingUserDetails;
    }
}
