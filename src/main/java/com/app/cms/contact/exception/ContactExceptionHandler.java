package com.app.cms.contact.exception;

import com.app.cms.common.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ContactExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ExceptionResponse> handleException(EmailAlreadyExistException ex, WebRequest request){
        ExceptionResponse response = ExceptionResponse.builder()
                .status(404)
                .error("Email already exist")
                .message(ex.getMessage())
                .path(request.getContextPath())
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
