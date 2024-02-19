package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateException extends ServiceException {

    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
