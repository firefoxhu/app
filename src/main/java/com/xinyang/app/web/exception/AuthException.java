package com.xinyang.app.web.exception;

import lombok.Getter;

public class AuthException extends RuntimeException {

    @Getter
    private final int code = 403;

    public AuthException(String message){
        super(message);
    }
}
