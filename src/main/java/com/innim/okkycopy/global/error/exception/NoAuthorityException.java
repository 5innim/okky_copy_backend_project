package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;

public class NoAuthorityException extends ServiceException {

    public NoAuthorityException(ErrorCode errorCode) { super(errorCode);}

}
