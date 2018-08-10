package com.xinyang.app.web.service.impl;

import com.google.common.collect.Maps;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.UserRepository;
import com.xinyang.app.web.service.UserService;
import com.xinyang.app.web.util.ChineseNameUtil;
import com.xinyang.app.web.wx.WxSession;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private XyProperties xyProperties;

    @Transactional
    @Override
    public Map<String, Object> registerOrLogin(WxSession wxSession){


        User user = userRepository.findOne((root, query, cb)->
                cb.and(cb.equal(root.get("openId"),wxSession.getOpenId()))
        ).map(x->userRepository.save(
                User.builder()
                        .id(x.getId())
                        .openId(x.getOpenId())
                        .anonymousName(x.getAnonymousName())
                        .anonymousAvatar(x.getAnonymousAvatar())
                        .nickname(x.getNickname())
                        .avatar(x.getAvatar())
                        .status(x.getStatus())
                .build()
        )).orElseGet(()->userRepository.save(
                User.builder()
                        .openId(wxSession.getOpenId())
                        .anonymousName(ChineseNameUtil.randomName())
                        .anonymousAvatar(xyProperties.getFileConfig().getImageServer()+"head_"+ RandomUtils.nextInt(1,10)+".png")
                        .status("0")
                        .build()
        ));

        // 模拟生成 3r_session
        String r_session = UUID.randomUUID().toString();
        Optional.ofNullable(
                wxSession.getExpired_session()
        ).map(e->

                redisTemplate.delete(wxSession.getExpired_session())
        ).orElse(false);

        // 放入缓存做登录状态
        redisTemplate.opsForValue().set(r_session,user,7, TimeUnit.DAYS);

        Map<String,Object> map = Maps.newHashMap();

        map.put("xy365_3rd_session",r_session);
        return map;
    }


}
