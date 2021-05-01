package com.jjcsa.util;

import com.jjcsa.model.Action;
import java.util.Date;

public class ActionUtil {

    public static Action buildAction(String toUserEmail,
                              String fromUserEmail,
                              String action) {
        return Action.builder()
                .toUserId(toUserEmail)
                .fromUserId(fromUserEmail)
                .action(action)
                .dateOfAction(new Date())
                .build();
    }
}
