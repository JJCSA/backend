package com.jjcsa.model.enumModel;

public enum Event {

    REGISTRATION("REGISTRATION"),
    FORGOT_PASSWORD("FORGOT_PASSWORD");

    private String name;

    Event(String name) {
        this.name = name;
    }

}
