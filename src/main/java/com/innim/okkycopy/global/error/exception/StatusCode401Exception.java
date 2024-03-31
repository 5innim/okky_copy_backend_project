package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCase;

public class StatusCode401Exception extends StatusCodeException {

    public StatusCode401Exception(ErrorCase errorCase) {
        super(errorCase);
    }
}
