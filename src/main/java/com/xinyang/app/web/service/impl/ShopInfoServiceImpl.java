package com.xinyang.app.web.service.impl;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Mchnt;
import com.xinyang.app.core.model.Shop;
import com.xinyang.app.core.model.ShopInfo;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.MchntRepository;
import com.xinyang.app.core.repository.ShopInfoRepository;
import com.xinyang.app.core.repository.ShopRepository;
import com.xinyang.app.core.util.FileUtil;
import com.xinyang.app.web.domain.form.ShopInfoForm;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.ShopInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ShopInfoServiceImpl implements ShopInfoService {

    @Autowired
    private ShopInfoRepository shopInfoRepository;

    @Autowired
    private MchntRepository mchntRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private XyProperties xyProperties;


    @Transactional
    @Override
    public Map<String, Object> viewShop(long shoInfoId) {

        shopInfoRepository.updateViewsCount(shoInfoId);

        Map<String,Object> map = Maps.newHashMap();

        return map;
    }
    @Transactional
    @Override
    public Map<String, Object> updateShopInfo(HttpServletRequest request, ShopInfoForm shopInfoForm) {

        log.info("正在修改门店信息(哪个不为空修改哪里)：{}",shopInfoForm);

        User user = Optional.ofNullable(redisTemplate.opsForValue().get(request.getHeader("Third-Session"))).map(u->(User)u).orElseThrow(()->new AuthException("纳秒之间的用户登录过期！万年一见。"));

        Mchnt mchnt = mchntRepository.findMchntByUserId(user.getId());

        Shop shop = shopRepository.findShopByMchntId(mchnt.getId());

        ShopInfo shopInfo = shopInfoRepository.findShopInfoByShopId(shop.getId());

        List<String> deleteFileNames = null;

        List<String> saveFileNames = null;

        if(shopInfoForm.getShopPhone() != null){

            shopInfo.setPhone(shopInfoForm.getShopPhone());

        }else if(shopInfoForm.getSkillDescription() != null){

            shopInfo.setSkillDescription(shopInfoForm.getSkillDescription());

        }else if(shopInfoForm.getBannerPicture() != null){

            deleteFileNames = Arrays.asList(shopInfo.getBannerPicture().split(","));
            saveFileNames = Arrays.asList(shopInfoForm.getBannerPicture().split(","));
            shopInfo.setBannerPicture(shopInfoForm.getBannerPicture());

        }else if(shopInfoForm.getDescription() != null) {

            shopInfo.setDescription(shopInfoForm.getDescription());

        }else if(shopInfoForm.getGalleryPicture() != null){

            if(shopInfo.getGalleryPicture() == null) {
                deleteFileNames = Arrays.asList();
            }else {
                deleteFileNames = Arrays.asList(shopInfo.getGalleryPicture().split(","));
            }
            saveFileNames = Arrays.asList(shopInfoForm.getGalleryPicture().split(","));
            shopInfo.setGalleryPicture(shopInfoForm.getGalleryPicture());
        }


        shopInfoRepository.save(shopInfo);

        // 图片处理
        if(deleteFileNames != null){
            // 删除云端图片
            deleteFileNames.parallelStream().forEach(i->
                    FileUtil.delete(i)
            );
        }

        if(saveFileNames != null){
            // 生成本地 缩略图 和 压缩图
            saveFileNames.parallelStream().forEach(i->
                    FileUtil.localUpload(xyProperties.getFileConfig().getTempDir(),i)
            );

            // 将缩略图 和 压缩图上传七牛云
            saveFileNames.parallelStream().forEach(i->
                    FileUtil.qiNiuUpload(xyProperties.getFileConfig().getTempDir(),i)
            );
        }

        return ResultMap.getInstance().put("msg","【系统提示】修改成功！").toMap();
    }

}
