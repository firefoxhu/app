package com.xinyang.app.web.service;
import com.xinyang.app.web.domain.form.MchntForm;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface MchntService{

    Map<String,Object> mchntCome(HttpServletRequest request, MchntForm mchntForm);

    Map<String,Object> mchntExist(String mobile);

    Map<String,Object> findMchntByUserId(HttpServletRequest request);

    Map<String,Object> checkUserBindingMchnt(HttpServletRequest request);
}
