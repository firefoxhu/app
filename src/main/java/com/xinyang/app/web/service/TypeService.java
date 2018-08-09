package com.xinyang.app.web.service;

import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TypeService{

    Map<String,Object> findTypeByCategoryCode(String[] codes, Pageable pageable);

    Map<String,Object> findTypeByCategory(String code);

}
