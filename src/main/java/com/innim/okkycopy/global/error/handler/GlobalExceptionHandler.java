package com.innim.okkycopy.global.error.handler;

import com.innim.okkycopy.global.enums.ErrorCode;
import com.innim.okkycopy.global.error.ErrorResponse;
import com.innim.okkycopy.global.error.exception.ServiceException;
import com.innim.okkycopy.global.utils.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
        WebRequest request) {

        String field = ex.getBindingResult().getFieldErrors().get(0).getField();
        ErrorCode errorCode = ValidationUtil.retreiveErrorCode(field);

        return ResponseEntity
            .status(errorCode.getStatus())
            .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

}
