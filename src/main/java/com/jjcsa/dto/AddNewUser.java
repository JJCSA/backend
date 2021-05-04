package com.jjcsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public @Data class AddNewUser {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String mobileNumber;
    private String prefMethodOfContact;
    private String jainCommunity;
}