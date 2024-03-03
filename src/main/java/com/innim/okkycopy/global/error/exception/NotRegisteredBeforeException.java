package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NotRegisteredBeforeException extends ServiceException {

    public NotRegisteredBeforeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
