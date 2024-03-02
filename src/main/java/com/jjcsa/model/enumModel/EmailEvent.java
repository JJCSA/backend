package com.jjcsa.model.enumModel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailEvent {

    REGISTRATION("REGISTRATION"),
    FORGOT_PW("FORGOT_PASSWORD"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    PW_UPDATE("PASSWORD_UPDATE"),
    PW_UPDATE_FAILED("PASSWORD_UPDATE_FAILED"),
    PROFILE_UPDATE("PROFILE_UPDATE"),
    CONTACT_US("CONTACT US");

    private String name;

}
