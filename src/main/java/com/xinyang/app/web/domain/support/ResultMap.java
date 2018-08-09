package com.xinyang.app.web.domain.support;

import com.google.common.collect.Maps;

import java.util.Map;

public class ResultMap {

    private Map<String,Object> map = Maps.newHashMap();

    public static ResultMap getInstance(){
        return new ResultMap();
    }

    public  ResultMap put(String key,Object value){
        map.put(key,value);
        return this;
    }

    public  Map<String,Object> toMap(){
        return map;
    }

}
