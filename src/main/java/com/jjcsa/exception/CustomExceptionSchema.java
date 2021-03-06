package com.jjcsa.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class CustomExceptionSchema {
    private String message;
    private String details;
    private String hint;
    private String nextActions;
    private String support;

    protected CustomExceptionSchema() {}

}
