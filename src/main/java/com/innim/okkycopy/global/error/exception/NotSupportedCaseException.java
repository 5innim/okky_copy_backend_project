package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NotSupportedCaseException extends ServiceException {

    public NotSupportedCaseException(ErrorCode errorCode) {
        super(errorCode);
    }
}
