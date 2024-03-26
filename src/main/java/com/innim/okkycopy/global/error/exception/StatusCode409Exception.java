package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCase;

public class StatusCode409Exception extends StatusCodeException {

    public StatusCode409Exception(ErrorCase errorCase) {
        super(errorCase);
    }
}
