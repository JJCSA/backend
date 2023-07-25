package com.jjcsa.model.enumModel;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

@Getter
public enum EmailEvent {

    REGISTRATION("REGISTRATION"),
    FORGOT_PW("FORGOT_PASSWORD"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    PW_UPDATE("PASSWORD_UPDATE"),
    PW_UPDATE_FAILED("PASSWORD_UPDATE_FAILED"),
    PROFILE_UPDATE("PROFILE_UPDATE");

    private static final Map<Action, EmailEvent> ADMIN_ACTION_TO_EMAIL_EVENT_MAP =
            ImmutableMap
                    .<Action, EmailEvent>builder()
                    .put(Action.APPROVE_USER, APPROVED)
                    .put(Action.REJECT_USER, REJECTED)
            .build();

    private String name;

    EmailEvent(String name) {
        this.name = name;
    }

    public static EmailEvent resolveEmailEventUsingAdminAction(Action adminAction) {
        return ADMIN_ACTION_TO_EMAIL_EVENT_MAP.get(adminAction);
    }

}
