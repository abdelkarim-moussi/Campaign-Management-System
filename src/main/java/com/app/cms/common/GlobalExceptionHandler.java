package com.app.cms.common;

import com.app.cms.contact.exception.EmailAlreadyExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException ex, WebRequest request){
        ExceptionResponse response = ExceptionResponse.builder()
                .status(404)
                .error("not found")
                .message(ex.getMessage())
                .path(request.getContextPath())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
