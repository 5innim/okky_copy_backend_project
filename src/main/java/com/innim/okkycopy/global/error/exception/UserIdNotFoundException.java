package com.innim.okkycopy.global.error.exception;

public class UserIdNotFoundException extends FailValidationJwtException {
    public UserIdNotFoundException(String message) {
        super(message);
    }
}
