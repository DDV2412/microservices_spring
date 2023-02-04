package com.ipmugo.articleservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HttpStatus statusCode;

    private final String message;

    public CustomException(String message, HttpStatus statusCode){
        this.message = message;
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
