package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private ErrorCode errorCode;
    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
