package com.innim.okkycopy.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    _400_INVALID_ID (HttpStatus.BAD_REQUEST, "400101", "input value of 'id' is invalid"),
    _400_INVALID_PW (HttpStatus.BAD_REQUEST, "400102", "input value of 'password' is invalid"),
    _400_INVALID_EMAIL (HttpStatus.BAD_REQUEST, "400103", "input value of 'email' is invalid"),
    _400_INVALID_NAME (HttpStatus.BAD_REQUEST, "400104", "input value of 'name' is invalid"),
    _400_INVALID_NICKNAME (HttpStatus.BAD_REQUEST, "400105", "input value of 'nickname' is invalid"),
    _400_INVALID_UNEXPECTED (HttpStatus.BAD_REQUEST, "400200", "unexpected input is invalid");

    private HttpStatus status;
    private String code;
    private String message;
}
