package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCase;

public class StatusCode403Exception extends StatusCodeException {

    public StatusCode403Exception(ErrorCase errorCase) {
        super(errorCase);
    }
}
