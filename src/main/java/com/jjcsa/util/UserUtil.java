package com.jjcsa.util;

import com.jjcsa.model.User;
import com.jjcsa.model.enumModel.UserRole;
import com.jjcsa.model.enumModel.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

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
}
