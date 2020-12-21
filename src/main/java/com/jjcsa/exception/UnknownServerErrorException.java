package com.jjcsa.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
public class UnknownServerErrorException extends RuntimeException{
    private String message;
    private String details;
    private String hint;
    private String nextActions;
    private String support;

    protected UnknownServerErrorException() {}
}
