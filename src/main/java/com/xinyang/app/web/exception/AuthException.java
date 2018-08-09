package com.xinyang.app.web.exception;

import lombok.Getter;

public class AuthException extends RuntimeException {

    @Getter
    private static final int code = 403;

    public AuthException(String message){
        super(message);
    }
}
