package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NoSuchPostException extends ServiceException {

    public NoSuchPostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
