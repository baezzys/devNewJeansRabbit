package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> handleRuntimeException(RuntimeException ex) {
        return ApiResult.failed("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
