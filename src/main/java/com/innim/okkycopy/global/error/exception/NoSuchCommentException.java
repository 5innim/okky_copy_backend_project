package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NoSuchCommentException extends ServiceException {

    public NoSuchCommentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
