package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class FailInitializationException extends ServiceException {
    public FailInitializationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
