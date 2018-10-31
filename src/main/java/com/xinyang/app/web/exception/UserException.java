package com.xinyang.app.web.exception;

import com.xinyang.app.web.enums.UserEnum;
import lombok.Getter;

@Getter
public class UserException extends  RuntimeException {

    private int code;

    private String message;

    public UserException(String message){
        super(message);
    }

    public UserException(UserEnum userEnum){
        super(userEnum.getMsg());
        this.code = userEnum.getCode();
        this.message = userEnum.getMsg();
    }

}
