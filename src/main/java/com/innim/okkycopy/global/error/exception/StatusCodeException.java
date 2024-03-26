package com.innim.okkycopy.global.error.exception;

import com.innim.okkycopy.global.error.ErrorCase;
import lombok.Getter;

@Getter
public class StatusCodeException extends RuntimeException {

    private final ErrorCase errorCase;

    public StatusCodeException(ErrorCase errorCase) {
        super(errorCase.getMessage());
        this.errorCase = errorCase;
    }
}
