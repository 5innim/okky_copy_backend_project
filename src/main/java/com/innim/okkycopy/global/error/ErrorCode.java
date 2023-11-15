package com.innim.okkycopy.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    _400_INVALID_ID (HttpStatus.BAD_REQUEST, "400001", "input value of 'id' is invalid"),
    _400_INVALID_PW (HttpStatus.BAD_REQUEST, "400002", "input value of 'password' is invalid"),
    _400_INVALID_EMAIL (HttpStatus.BAD_REQUEST, "400003", "input value of 'email' is invalid"),
    _400_INVALID_NAME (HttpStatus.BAD_REQUEST, "400004", "input value of 'name' is invalid"),
    _400_INVALID_NICKNAME (HttpStatus.BAD_REQUEST, "400005", "input value of 'nickname' is invalid"),
    _400_INVALID_UNEXPECTED (HttpStatus.BAD_REQUEST, "400100", "unexpected input is invalid"),
    _400_BAD_CREDENTIALS (HttpStatus.BAD_REQUEST, "400011", "'id' or 'password' is incorrect"),
    _400_AUTHENTICATION_FAILED (HttpStatus.BAD_REQUEST, "400012", "authentication is failed"),
    _400_INVALID_TOKEN_VALUE(HttpStatus.BAD_REQUEST, "400013", "invalid token value"),
    _401_TOKEN_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "401001", "this token is not valid"),
    _403_TOKEN_EXPIRED (HttpStatus.FORBIDDEN, "403001", "token is expired"),
    _409_DUPLICATE_ID (HttpStatus.CONFLICT, "409001", "input value of 'id' is duplicated"),
    _409_DUPLICATE_EMAIL (HttpStatus.CONFLICT, "409002", "input value of 'email' is duplicated"),
    _500_GENERATE_TOKEN (HttpStatus.INTERNAL_SERVER_ERROR, "500001", "can not generate token"),
    _500_FAIL_INITIALIZATION (HttpStatus.INTERNAL_SERVER_ERROR, "500002", "database was not initialized");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
