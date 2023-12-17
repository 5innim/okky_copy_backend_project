package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NoSuchScrapException extends ServiceException {

    public NoSuchScrapException(ErrorCode errorCode) {
        super(errorCode);
    }
}
