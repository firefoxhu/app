package com.xinyang.app.web.enums;

import lombok.Getter;

public enum UserEnum {

    USER_NOT_ACCOUNT_FOUND(5555,"登录的用户名不存在！"),
    USER_PASSWORD_ERROR(4444,"您输入的密码有误！"),
    USER_ACCOUNT_ALREADY(3333,"账号已经存在！");

    @Getter
    private int code;
    @Getter
    private String msg;


    UserEnum(int code ,String msg){
        this.code = code;
        this.msg = msg;
    }


    UserEnum(UserEnum userEnum){
        this.code = userEnum.code;
        this.msg = userEnum.msg;
    }
}
