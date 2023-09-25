package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.enums.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateException extends ServiceException {

    private ErrorCode errorCode;
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }
}
