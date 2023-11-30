package com.innim.okkycopy.global.error.handler;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.ErrorResponse;
import com.innim.okkycopy.global.error.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
        ErrorCode errorCode = SignupRequest.retreiveErrorCode(field);

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ErrorCode._400_DATA_INTEGRITY_VIOLATION.getCode(), ex.getMessage()));
    }

}
