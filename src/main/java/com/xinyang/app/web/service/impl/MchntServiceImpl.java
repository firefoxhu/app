package com.xinyang.app.web.service.impl;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.*;
import com.xinyang.app.core.plugin.SmsSender;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.*;
import com.xinyang.app.core.util.FileUtil;
import com.xinyang.app.web.domain.dto.ShopDTO;
import com.xinyang.app.web.domain.dto.TypeDTO;
import com.xinyang.app.web.domain.form.MchntForm;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.enums.MchntEnum;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.exception.MchntUnBindingException;
import com.xinyang.app.web.service.MchntService;
import com.xinyang.app.web.service.MessageService;
import com.xinyang.app.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MchntServiceImpl implements MchntService {

    @Autowired
    private ShopInfoRepository shopInfoRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private MchntRepository mchntRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private ShopInfoTypeRepository shopInfoTypeRepository;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private XyProperties xyProperties;


    @Autowired
    private MessageService messageService;

    @Transactional
    @Override
    public Map<String, Object> mchntCome(HttpServletRequest request, MchntForm mchntForm){

        String xcxSession = request.getHeader("Third-Session");
        String appSession = request.getHeader("App-Session");
        User user;
        try {
            String redis_key = UserService.USER_SESSION + (xcxSession == null ? appSession : xcxSession);
            user = (User) redisTemplate.opsForValue().get(redis_key);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if(user == null) {
            throw new AuthException("业务缓存读取用户信息失败【需要重新登录】！");
        }

        // 判断当前微信号是否绑定过商户了
        Mchnt wxBindMchnt = mchntRepository.findMchntByUserId(user.getId());

        if(wxBindMchnt != null){
            throw new RuntimeException("抱歉一个微信号只能入住/绑定一个商户！");
        }
        // TODO 验证码接口暂时不实现
        // Map<String,Object> validateResult =  smsSender.validate(mchntForm.getMchntPhone(),mchntForm.getSmsCode());

        //        if(!(Boolean) validateResult.get("status")){
        //            throw new RuntimeException(validateResult.get("message").toString());
        //        }
        Shop shopExist = shopRepository.findShopByName(mchntForm.getShopName());

        if(shopExist != null){
            throw new RuntimeException("抱歉门店名称已经被占用！");
        }

        Mchnt mchntExist = mchntRepository.findMchntByPhone(mchntForm.getMchntPhone());

        if(mchntExist != null){
            throw new RuntimeException("抱歉该手机号已被占用！");
        }

        Mchnt mchnt = mchntRepository.save(
            Mchnt.builder().name(mchntForm.getMchntName()).phone(mchntForm.getMchntPhone()).userId(user.getId()).build()
        );
        Shop shop = shopRepository.save(
            Shop.builder().province(
                    Optional.ofNullable(StringUtils.substringBetween(mchntForm.getAddress(),"","省")).map(x->x.concat("省")).orElse("河南省")
            ).city(
                    Optional.ofNullable(StringUtils.substringBetween(mchntForm.getAddress(),"省","市")).map(x->x.concat("市")).orElse("信阳市")
            ).area(
                    Optional.ofNullable(StringUtils.substringBetween(mchntForm.getAddress(),"市","区")).map(x->x.concat("区")).orElse("平桥区")
            ).street(
                    Optional.ofNullable(StringUtils.substringAfter(mchntForm.getAddress(),"区")).orElse("").concat("[").concat(mchntForm.getAddressDescription()).concat("]")
            ).latitude(mchntForm.getLatitude())
            .longitude(mchntForm.getLongitude())
            .name(mchntForm.getShopName())
            .mchntId(mchnt.getId()).build()
        );
        ShopInfo shopInfo = shopInfoRepository.save(
            ShopInfo.builder().bannerPicture(mchntForm.getBannerPicture()).phone(mchntForm.getMchntPhone()).skillDescription(mchntForm.getSkillDescription()).categoryCode(mchntForm.getCategoryCode())
            .shopId(
                    shop.getId()
            ).build()
        );

        // 图片处理
        if(mchntForm.getBannerPicture() != null){
            // 生成本地 缩略图 和 压缩图
            Arrays.asList(mchntForm.getBannerPicture().split(",")).parallelStream().forEach(i->
                    FileUtil.localUpload(xyProperties.getFileConfig().getTempDir(),i)
            );

            // 将缩略图 和 压缩图上传七牛云
            Arrays.asList(mchntForm.getBannerPicture().split(",")).parallelStream().forEach(i->
                    FileUtil.qiNiuUpload(xyProperties.getFileConfig().getTempDir(),i)
            );
        }

        messageService.notice(user.getId(),"【系统消息】您已成功入驻！");

        Map<String,Object> map = Maps.newHashMap();
        map.put("message","入住成功！");
        return map;
    }

    @Override
    public Map<String, Object> mchntExist(String mobile) {

        Mchnt mchnt = mchntRepository.findMchntByPhone(mobile);
        log.info("检查商户手机号是否被占用：{} , {}",mobile,mchnt);

        Map<String,Object> map = Maps.newHashMap();
        if(mchnt == null) {
            map.put("message","恭喜商户注册手机号可用！");
        }else {
            throw new RuntimeException("抱歉商户注册手机号已经占用");
        }
        return map;
    }

    @Override
    public Map<String, Object> findMchntByUserId(HttpServletRequest request) {
        String xcxSession = request.getHeader("Third-Session");
        String appSession = request.getHeader("App-Session");
        User user;
        try {
            String redis_key = UserService.USER_SESSION + (xcxSession == null ? appSession : xcxSession);
            user = (User) redisTemplate.opsForValue().get(redis_key);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if(user == null) {
            throw new AuthException("业务缓存读取用户信息失败【需要重新登录】！");
        }

        Mchnt mchnt = Optional.ofNullable(mchntRepository.findMchntByUserId(user.getId())).orElseThrow(()->new MchntUnBindingException(MchntEnum.MCHNT_UNBIND));

        Shop shop = shopRepository.findShopByMchntId(mchnt.getId());

        ShopInfo shopInfo = shopInfoRepository.findShopInfoByShopId(shop.getId());

        Category category = categoryRepository.findCategoryByCode(shopInfo.getCategoryCode());

        List<Long> typeIds = shopInfoTypeRepository.findShopInfoTypeByShopInfoId(shopInfo.getId()).stream().map(ShopInfoType::getTypeId).collect(Collectors.toList());

        ShopDTO shopDTO = ShopDTO.builder()
                .shopId(shop.getId())
                .shopInfoId(shopInfo.getId())
                .shopName(shop.getName())
                .mchntPhone(mchnt.getPhone())
                .mchntName(mchnt.getName())
                .address(shop.getProvince()+shop.getCity()+shop.getArea()+shop.getStreet())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .shopCategoryCode(shopInfo.getCategoryCode())
                .shopCategoryName(category.getName())
                .bannerPicture(
                        Optional.ofNullable(shopInfo.getBannerPicture()).map( g->
                                Stream.of(StringUtils.split(shopInfo.getBannerPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                ).collect(Collectors.toList())
                        ).orElse(Arrays.asList())
                )
                .galleryPicture(
                        Optional.ofNullable(shopInfo.getGalleryPicture()).map( g->
                                Stream.of(StringUtils.split(shopInfo.getGalleryPicture(),",")).map(x->xyProperties.getFileConfig().getImageServer()+x
                                ).collect(Collectors.toList())
                        ).orElse(Arrays.asList())
                )
                .shopSkillDescription(shopInfo.getSkillDescription())
                .description(shopInfo.getDescription())
                .shopPhone(shopInfo.getPhone())
                .typeDTOS(
                        typeRepository.findTypeByIdIn(typeIds).stream().map(e->
                                TypeDTO.builder().typeId(e.getId()).name(e.getName()).build()
                        ).collect(Collectors.toList())
                )
        .build();
        Map<String,Object> map = Maps.newHashMap();
        map.put("data",shopDTO);
        return map;
    }

    @Override
    public Map<String, Object> checkUserBindingMchnt(HttpServletRequest request) {
        String xcxSession = request.getHeader("Third-Session");
        String appSession = request.getHeader("App-Session");
        User user;
        try {
            String redis_key = UserService.USER_SESSION + (xcxSession == null ? appSession : xcxSession);
            user = (User) redisTemplate.opsForValue().get(redis_key);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if(user == null) {
            throw new AuthException("业务缓存读取用户信息失败【需要重新登录】！");
        }
        Mchnt mchnt = Optional.ofNullable(mchntRepository.findMchntByUserId(user.getId())).orElseThrow(()->new MchntUnBindingException(MchntEnum.MCHNT_UNBIND));
        return ResultMap.getInstance().put("message","已绑定").toMap();
    }
}
