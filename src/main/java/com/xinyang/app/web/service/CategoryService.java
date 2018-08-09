package com.xinyang.app.web.service;
import java.util.Map;

public interface CategoryService{

    Map<String,Object> findAll();

    Map<String,Object> findCategoryByCode(String[] codes);

    Map<String,Object> findCategoryWithType();

}
