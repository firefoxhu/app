package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.Shop;
public interface ShopRepository extends XyRepository<Shop> {

    Shop findShopByName(String name);

    Shop findShopByMchntId(Long mchntId);

}
