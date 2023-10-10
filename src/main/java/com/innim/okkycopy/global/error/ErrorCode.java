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
    _409_DUPLICATE_ID (HttpStatus.CONFLICT, "409001", "input value of 'id' is duplicated"),
    _409_DUPLICATE_EMAIL (HttpStatus.CONFLICT, "409002", "input value of 'email' is duplicated");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
