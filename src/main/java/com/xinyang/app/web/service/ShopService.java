package com.xinyang.app.web.service;
import com.xinyang.app.web.domain.conditon.ShopCondition;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ShopService {


    Map<String,Object> findShopByCondition(ShopCondition condition, Pageable pageable);

    Map<String,Object> checkShopNameExist(String shopName);

    Map<String,Object> findShopByShopInfoId(long shopInfoId);


    Map<String,Object> findShopByType(long typeId, Pageable pageable);
}
