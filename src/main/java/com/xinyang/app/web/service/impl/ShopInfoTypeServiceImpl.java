package com.xinyang.app.web.service.impl;
import com.xinyang.app.core.model.*;
import com.xinyang.app.core.repository.MchntRepository;
import com.xinyang.app.core.repository.ShopInfoRepository;
import com.xinyang.app.core.repository.ShopInfoTypeRepository;
import com.xinyang.app.core.repository.ShopRepository;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.ShopInfoTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopInfoTypeServiceImpl implements ShopInfoTypeService {

    @Autowired
    private ShopInfoRepository shopInfoRepository;
    @Autowired
    private MchntRepository mchntRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopInfoTypeRepository shopInfoTypeRepository;


    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Map<String, Object> bindingType(HttpServletRequest request,String typeIds) {

        User user = Optional.ofNullable(redisTemplate.opsForValue().get(request.getHeader("xy365_3rd_session"))).map(u->(User)u).orElseThrow(()->new AuthException("纳秒之间的用户登录过期！万年一见。"));

        Mchnt mchnt = mchntRepository.findMchntByUserId(user.getId());

        Shop shop = shopRepository.findShopByMchntId(mchnt.getId());

        ShopInfo shopInfo = shopInfoRepository.findShopInfoByShopId(shop.getId());



        shopInfoTypeRepository.deleteAll(
                shopInfoTypeRepository.findShopInfoTypeByShopInfoId(shopInfo.getId())
        );

        shopInfoTypeRepository.saveAll(
                Arrays.asList(typeIds.split(",")).stream().map(x-> ShopInfoType.builder().typeId(Long.parseLong(x)).shopInfoId(shopInfo.getId()).build()).collect(Collectors.toList())
        );


        return ResultMap.getInstance().put("msg","修改成功！").toMap();
    }
}
