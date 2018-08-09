package com.xinyang.app.web.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ShopInfoTypeService {

    Map<String,Object> bindingType(HttpServletRequest request, String typeIds);


}
