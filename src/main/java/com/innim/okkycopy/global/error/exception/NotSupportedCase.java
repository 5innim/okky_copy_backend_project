package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NotSupportedCase extends ServiceException {

    public NotSupportedCase(ErrorCode errorCode) {
        super(errorCode);
    }
}
