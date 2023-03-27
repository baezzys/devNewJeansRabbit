package com.devJeans.rabbit.controller;

import com.devJeans.rabbit.bind.ApiResult;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> handleRuntimeException(RuntimeException ex, HttpServletResponse response) {
        response.setStatus(500);
        return ApiResult.failed(ex.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public ApiResult<?> handleJwtException(JwtException ex, HttpServletResponse response) {
        response.setStatus(403);
        return ApiResult.failed("Google id token이 만료되었습니다.", 403);
    }
}
