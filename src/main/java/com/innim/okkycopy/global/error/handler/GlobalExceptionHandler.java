package com.innim.okkycopy.global.error.handler;

import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.ErrorResponse;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleStatusCodeException(StatusCodeException ex) {
        ErrorCase errorCase = ex.getErrorCase();
        return ResponseEntity
            .status(errorCase.getStatus())
            .body(new ErrorResponse(errorCase.getCode(), errorCase.getMessage()));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ErrorCase._409_DATA_INTEGRITY_VIOLATION.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ErrorCase errorCase = ErrorCase._400_FILE_SIZE_EXCEEDED;
        return ResponseEntity
            .status(errorCase.getStatus())
            .body(new ErrorResponse(errorCase.getCode(), errorCase.getMessage()));
    }

}
