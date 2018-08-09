package com.xinyang.app.web.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtil {


    public static String  calculateTime(LocalDateTime localDateTime)  {
        long agoSecond = localDateTime.toEpochSecond(ZoneOffset.of("+8"));

        long nowSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));

        long calceSceond = nowSecond - agoSecond;

        if(calceSceond < 60) {
            System.out.println(calceSceond);
            return "刚刚";
        }else if(60 <= calceSceond && calceSceond < 60*60){ // 一小时
            return calceSceond/60 + "分钟前";
        }else if( 60*60 <= calceSceond &&  calceSceond < 60*60*24) { // 24小时之内
           return calceSceond/(60*60) + "小时前";
        }else if(60*60*24 <= calceSceond && calceSceond < 60*60*24*3){
            return calceSceond/(60*60*24) + "天前";
        }

        return  formatDateTime(localDateTime);
    }


    public static String formatDateTime(LocalDateTime localDateTime){
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
