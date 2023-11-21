package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NoSuchTopicException extends ServiceException {

    public NoSuchTopicException(ErrorCode errorCode) {
        super(errorCode);
    }
}
