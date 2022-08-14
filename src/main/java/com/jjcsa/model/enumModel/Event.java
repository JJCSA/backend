package com.jjcsa.model.enumModel;

import java.util.HashMap;
import java.util.Map;

public enum Event {

    REGISTRATION("REGISTRATION"),
    FORGOT_PASSWORD("FORGOT_PASSWORD"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private static final Map<Action, Event> ADMIN_ACTION_TO_EMAIL_EVENT_MAP = new HashMap<Action, Event>(){{
       put(Action.APPROVE_USER, APPROVED);
       put(Action.REJECT_USER, REJECTED);
    }};

    private String name;

    Event(String name) {
        this.name = name;
    }

    public static Event resolveEmailEventUsingAdminAction(Action adminAction) {
        return ADMIN_ACTION_TO_EMAIL_EVENT_MAP.get(adminAction);
    }

}
