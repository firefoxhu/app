package com.xinyang.app.web.service.impl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Shop;
import com.xinyang.app.core.model.ShopInfo;
import com.xinyang.app.core.model.ShopInfoType;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.MchntRepository;
import com.xinyang.app.core.repository.ShopInfoRepository;
import com.xinyang.app.core.repository.ShopInfoTypeRepository;
import com.xinyang.app.core.repository.ShopRepository;
import com.xinyang.app.web.domain.conditon.ShopCondition;
import com.xinyang.app.web.domain.dto.ShopDTO;
import com.xinyang.app.web.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopInfoRepository shopInfoRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MchntRepository mchntRepository;

    @Autowired
    private ShopInfoTypeRepository shopInfoTypeRepository;

    @Autowired
    private XyProperties xyProperties;

    @Override
    public Map<String, Object> findShopByCondition(ShopCondition condition, Pageable pageable) {


         Page<ShopInfo> page = shopInfoRepository.findAll((root, query, cb)-> {

                    List<Predicate> list = Lists.newArrayList();
                    Optional.ofNullable(condition.getCategoryCode()).ifPresent(x ->list.add(cb.equal(root.get("categoryCode"),condition.getCategoryCode())));
                    Optional.ofNullable(condition.getModel()).ifPresent(x ->{
                        if(StringUtils.equalsIgnoreCase(x,"RECOMMEND")){
                            list.add(cb.equal(root.get("recommend"),"1"));
                        }

                        if(StringUtils.equalsIgnoreCase(x,"TOP")){
                            list.add(cb.equal(root.get("topPlacement"),"1"));
                        }

                        if(StringUtils.equalsIgnoreCase(x,"DEFAULT")){

                        }
                    });
                    Predicate[] p = new Predicate[list.size()];
                    return cb.and(list.toArray(p));
                }
         ,pageable);
        Map<String,Object> map = Maps.newHashMap();
        map.put("hasNext",page.hasNext());
        map.put("list",page.getContent().parallelStream().map(s-> {
                    Shop shop = shopRepository.findById(s.getShopId()).get();
                    // Mchnt mchnt = mchntRepository.findById(shop.getMchntId()).get();
                    ShopDTO shopDTO = ShopDTO.builder()
                            .shopInfoId(s.getId())
                            .shopName(shop.getName())
                            .shopSkillDescription(s.getSkillDescription())
                            .address(shop.getProvince() + shop.getCity() + shop.getArea() + shop.getStreet())
                            .longitude(shop.getLongitude())
                            .latitude(shop.getLatitude())
                            .description(s.getDescription())
                            .views(s.getViews())
                            .mchntPhone(s.getPhone())
                            .bannerPicture(
                                    Optional.ofNullable(s.getBannerPicture()).map( g->
                                                Stream.of(StringUtils.split(s.getBannerPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                            ).collect(Collectors.toList())
                                    ).orElse(Arrays.asList())
                            )
                            .galleryPicture(
                                    Optional.ofNullable(s.getGalleryPicture()).map( g->
                                            Stream.of(StringUtils.split(s.getGalleryPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                            ).collect(Collectors.toList())
                                    ).orElse(Arrays.asList())
                            )
                            .build();
                    return shopDTO;
                }
        ).collect(Collectors.toList()));
        return map;
    }

    @Override
    public Map<String, Object> checkShopNameExist(String shopName) {



       Shop shop = shopRepository.findShopByName(shopName);
       log.info("检查门店名称是否被占用：{} , {}",shopName,shop);

        Map<String,Object> map = Maps.newHashMap();
       if(shop == null) {
           map.put("message","恭喜门店名称可用！");
       }else {
           throw new RuntimeException("抱歉门店名称已经占用");
       }

        return map;
    }

    @Override
    public Map<String, Object> findShopByShopInfoId(long shopInfoId) {
        Map<String,Object> map = Maps.newHashMap();

        map.put("list",shopInfoRepository.findById(shopInfoId).map(s->
            shopRepository.findById(s.getShopId()).map(shop->
                    ShopDTO.builder()
                            .shopInfoId(s.getId())
                            .shopName(shop.getName())
                            .shopSkillDescription(s.getSkillDescription())
                            .address(shop.getProvince() + shop.getCity() + shop.getArea() + shop.getStreet())
                            .longitude(shop.getLongitude())
                            .latitude(shop.getLatitude())
                            .description(s.getDescription())
                            .views(s.getViews())
                            .mchntPhone(s.getPhone())
                            .shopPhone(s.getPhone())
                            .bannerPicture(
                                    Optional.ofNullable(s.getBannerPicture()).map( g->
                                            Stream.of(StringUtils.split(s.getBannerPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                            ).collect(Collectors.toList())
                                    ).orElse(Arrays.asList())
                            )
                            .galleryPicture(
                                    Optional.ofNullable(s.getGalleryPicture()).map( g->
                                            Stream.of(StringUtils.split(s.getGalleryPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                            ).collect(Collectors.toList())
                                    ).orElse(Arrays.asList())
                            )
                            .build()
            ).orElseThrow(()-> new RuntimeException("该商户信息不完整！"))
        ).orElseThrow(()-> new RuntimeException("该商户不存在")));

        return map;
    }

    @Override
    public Map<String, Object> findShopByType(long typeId,Pageable pageable) {

        Page<ShopInfoType> shopInfoTypePage = shopInfoTypeRepository.findAll((root, query, cb)->
                        cb.and(cb.equal(root.get("typeId"),typeId))
       ,pageable);

        Map<String,Object> map = Maps.newHashMap();
        map.put("hasNext",shopInfoTypePage.hasNext());
        map.put("list",shopInfoRepository.findAllById(
                shopInfoTypePage.getContent().stream().map(x->x.getShopInfoId()).collect(Collectors.toList())
        ).parallelStream().map(s-> {
                    Shop shop = shopRepository.findById(s.getShopId()).get();
                    // Mchnt mchnt = mchntRepository.findById(shop.getMchntId()).get();
                    ShopDTO shopDTO = ShopDTO.builder()
                            .shopInfoId(s.getId())
                            .shopName(shop.getName())
                            .shopSkillDescription(s.getSkillDescription())
                            .address(shop.getProvince() + shop.getCity() + shop.getArea() + shop.getStreet())
                            .longitude(shop.getLongitude())
                            .latitude(shop.getLatitude())
                            .description(s.getDescription())
                            .views(s.getViews())
                            .mchntPhone(s.getPhone())
                            .bannerPicture(
                                    Optional.ofNullable(s.getBannerPicture()).map( g->
                                            Stream.of(StringUtils.split(s.getBannerPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                            ).collect(Collectors.toList())
                                    ).orElse(Arrays.asList())
                            )
                            .galleryPicture(
                                    Optional.ofNullable(s.getGalleryPicture()).map( g->
                                            Stream.of(StringUtils.split(s.getGalleryPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                            ).collect(Collectors.toList())
                                    ).orElse(Arrays.asList())
                            )
                            .build();
                    return shopDTO;
                }
        ).collect(Collectors.toList()));
        return map;
    }


}
