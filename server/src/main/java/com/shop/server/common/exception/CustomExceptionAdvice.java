package com.shop.server.common.exception;


import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class CustomExceptionAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> validatedExceptionHandler(ConstraintViolationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
	@ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionCode> exceptionHandler(CustomException e){
        return new ResponseEntity<>(e.getCode(), e.getCode().getHttpStatus());
    }
}
