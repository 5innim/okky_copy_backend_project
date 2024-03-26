package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCase;

public class StatusCode400Exception extends StatusCodeException {

    public StatusCode400Exception(ErrorCase errorCase) {
        super(errorCase);
    }
}
