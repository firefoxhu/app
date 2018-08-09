package com.xinyang.app.web.service;
import com.xinyang.app.web.domain.form.ShopInfoForm;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ShopInfoService{

    Map<String,Object> viewShop(long shoInfoId);

    Map<String,Object> updateShopInfo(HttpServletRequest request, ShopInfoForm shopInfoForm);

}
