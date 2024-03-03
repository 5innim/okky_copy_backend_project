package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class AlreadyExistExpressionException extends ServiceException{
    public AlreadyExistExpressionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
