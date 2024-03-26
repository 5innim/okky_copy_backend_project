package com.innim.okkycopy.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    _400_BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "400011", "'id' or 'password' is incorrect"),
    _400_AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "400012", "authentication is failed"),
    _400_INVALID_TOKEN_VALUE(HttpStatus.BAD_REQUEST, "400013", "invalid token value"),
    _400_NO_SUCH_TOPIC(HttpStatus.BAD_REQUEST, "400020", "can not find such topic"),
    _400_NO_SUCH_POST(HttpStatus.BAD_REQUEST, "400021", "can not find such post"),
    _400_NO_SUCH_SCRAP(HttpStatus.BAD_REQUEST, "400022", "did not scrap the post before"),
    _400_NO_SUCH_COMMENT(HttpStatus.BAD_REQUEST, "400023", "can not find such comment"),
    _400_ALREADY_EXIST_EXPRESSION(HttpStatus.BAD_REQUEST, "400024", "you've already expressed"),
    _400_NOT_REGISTERED_BEFORE(HttpStatus.BAD_REQUEST, "400025", "you should make expression before remove"),
    _400_NOT_SUPPORTED_CASE(HttpStatus.BAD_REQUEST, "400026", "the feature not support in this case"),
    _400_FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "400027", "file size is exceeded"),
    _400_DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "400030", "data integrity violation"),
    _400_INVALID_UNEXPECTED(HttpStatus.BAD_REQUEST, "400100", "unexpected input is invalid"),
    _401_TOKEN_AUTHENTICATION_FAIL(HttpStatus.UNAUTHORIZED, "401001", "this token is not valid"),
    _403_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "403001", "token is expired"),
    _403_NO_AUTHORITY(HttpStatus.FORBIDDEN, "403002", "this member has no authority for this resource"),
    _409_DUPLICATE_ID(HttpStatus.CONFLICT, "409001", "input value of 'id' is duplicated"),
    _409_DUPLICATE_EMAIL(HttpStatus.CONFLICT, "409002", "input value of 'email' is duplicated"),
    _500_GENERATE_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "500001", "can not generate token"),
    _500_FAIL_INITIALIZATION(HttpStatus.INTERNAL_SERVER_ERROR, "500002", "database was not initialized"),
    _500_FILE_NOT_CREATED(HttpStatus.INTERNAL_SERVER_ERROR, "500003", "temporal file is can not created"),
    _500_FAIL_PUT_S3(HttpStatus.INTERNAL_SERVER_ERROR, "500004", "fail file put to S3");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
