package com.jjcsa.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleException extends RuntimeException{
    private String message;
    private String details;
    private String hint;
    private String nextActions;
    private String support;
}
