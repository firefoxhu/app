package com.xinyang.app.web.enums;
import lombok.Getter;

public enum MchntEnum {

    MCHNT_UNBIND(10001,"用户没有绑定商户！");

    @Getter
    private int code;
    @Getter
    private String msg;


    MchntEnum(int code ,String msg){
        this.code = code;
        this.msg = msg;
    }


    MchntEnum(MchntEnum mchntEnum){
        this.code = mchntEnum.code;
        this.msg = mchntEnum.msg;
    }
}
