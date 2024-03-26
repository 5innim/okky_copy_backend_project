package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCase;

public class StatusCode500Exception extends StatusCodeException {

    public StatusCode500Exception(ErrorCase errorCase) {
        super(errorCase);
    }
}
