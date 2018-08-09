package com.xinyang.app.core.repository;

import com.xinyang.app.core.model.ShopInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopInfoRepository extends XyRepository<ShopInfo>{

    @Modifying
    @Query(value = "update xy_shop_info set views = views + 1 where id= :id",nativeQuery = true)
    int updateViewsCount(@Param("id") long id);

    ShopInfo findShopInfoByShopId(Long shopId);



}
