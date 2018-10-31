package com.xinyang.app.web.domain.support;

import lombok.Builder;
import lombok.Data;
import net.bytebuddy.utility.RandomString;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Builder
@Data
public class SimpleResponse<T>{

    private int code;

    private String msg;

    private Long timestamp;

    private String nonceStr;

    private T data;

    public static <T> SimpleResponse success(){
        return SimpleResponse.builder().code(0).msg("请求成功！").timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).build();
    }

    public static <T> SimpleResponse success(T data){
        return SimpleResponse.builder().code(0).msg("请求成功！").timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).data(data).build();
    }

    public static <T> SimpleResponse success(T data,String msg){
        return SimpleResponse.builder().code(0).msg(msg).timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).data(data).build();
    }

    public static <T> SimpleResponse fail(T data){
        return SimpleResponse.builder().code(-1).msg("失败！").timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).data(data).build();
    }

    public static <T> SimpleResponse fail(T data,String msg){
        return SimpleResponse.builder().code(-1).msg(msg).timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).data(data).build();
    }

    public static <T> SimpleResponse error(String msg){
        return SimpleResponse.builder().code(-1).msg(msg).timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).build();
    }

    public static <T> SimpleResponse error(int code,String msg){
        return SimpleResponse.builder().code(code).msg(msg).timestamp( LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"))).nonceStr(RandomString.make(RandomString.DEFAULT_LENGTH)).build();
    }

}
