package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.ShopInfoType;

import java.util.List;

public interface ShopInfoTypeRepository extends XyRepository<ShopInfoType> {

    List<ShopInfoType> findShopInfoTypeByShopInfoId(Long shopInfoId);


}
