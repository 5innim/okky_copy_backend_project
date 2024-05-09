package com.innim.okkycopy.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCase {
    _400_NO_SUCH_TOPIC(HttpStatus.BAD_REQUEST, "400020", "can not find such topic"),
    _400_NO_SUCH_POST(HttpStatus.BAD_REQUEST, "400021", "can not find such post"),
    _400_NO_SUCH_SCRAP(HttpStatus.BAD_REQUEST, "400022", "did not scrap the post before"),
    _400_NO_SUCH_COMMENT(HttpStatus.BAD_REQUEST, "400023", "can not find such comment"),
    _400_NO_ACCEPTABLE_PARAMETER(HttpStatus.BAD_REQUEST, "400024", "there is no parameter or can't handle"),
    _400_ALREADY_EXIST_EXPRESSION(HttpStatus.BAD_REQUEST, "400024", "you've already expressed"),
    _400_NOT_REGISTERED_BEFORE(HttpStatus.BAD_REQUEST, "400025", "you should make expression before remove"),
    _400_NOT_SUPPORTED_CASE(HttpStatus.BAD_REQUEST, "400026", "the feature not support in this case"),
    _400_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "400027", "file size is exceeded"),
    _400_DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "400030", "data integrity violation"),
    _400_BAD_FORM_DATA(HttpStatus.BAD_REQUEST, "400031", "input form data is wrong"),
    _400_INVALID_UNEXPECTED(HttpStatus.BAD_REQUEST, "400100", "unexpected input is invalid"),
    _401_TOKEN_VALIDATE_FAIL(HttpStatus.UNAUTHORIZED, "401001", "this token is not valid"),
    _401_LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "401002", "login fail"),
    _401_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401003", "token is expired"),
    _401_NOT_MOST_RECENT_GENERATED_TOKEN(HttpStatus.UNAUTHORIZED, "401004",
        "this refresh token is not most recent generated"),
    _401_NO_SUCH_MEMBER(HttpStatus.UNAUTHORIZED, "401005", "this member is not present"),
    _401_NOT_EXIST_SESSION(HttpStatus.UNAUTHORIZED, "401006", "session is not exist"),
    _403_NO_AUTHORITY(HttpStatus.FORBIDDEN, "403002", "this member has no authority for this resource"),
    _409_DUPLICATE_ID(HttpStatus.CONFLICT, "409001", "input value of 'id' is duplicated"),
    _409_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "409002", "input value of 'email' is duplicated"),
    _500_JWT_GENERATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "500001", "can not generate token"),
    _500_FAIL_INITIALIZATION(HttpStatus.INTERNAL_SERVER_ERROR, "500002", "database was not initialized"),
    _500_FILE_NOT_CREATED(HttpStatus.INTERNAL_SERVER_ERROR, "500003", "temporal file is can not created"),
    _500_FAIL_PUT_S3(HttpStatus.INTERNAL_SERVER_ERROR, "500004", "fail file put to S3");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
