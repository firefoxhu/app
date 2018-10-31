package com.xinyang.app.web.service.impl;

import com.google.common.collect.Maps;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.UserRepository;
import com.xinyang.app.web.domain.form.UserForm;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.enums.UserEnum;
import com.xinyang.app.web.exception.UserException;
import com.xinyang.app.web.service.UserService;
import com.xinyang.app.web.util.ChineseNameUtil;
import com.xinyang.app.web.login.wx.WxSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
        )).orElseGet(()-> userRepository.save(
                User.builder()
                        .openId(wxSession.getOpenId())
                        .anonymousName(ChineseNameUtil.randomName())
                        .anonymousAvatar(xyProperties.getFileConfig().getImageServer()+"head_"+ RandomUtils.nextInt(1,10)+".png")
                        .avatar(xyProperties.getFileConfig().getImageServer()+"head_"+ RandomUtils.nextInt(1,10)+".png")
                        .status("0")
                        .build())
        );

        Optional.ofNullable(wxSession.getExpired_session()).map(e-> redisTemplate.delete(USER_SESSION + wxSession.getExpired_session())).orElse(false);

        // 注册成功写入缓存 即为登录状态
        String randomCookie = DigestUtils.md5Hex(UUID.randomUUID().toString().replace("-",""));
        redisTemplate.opsForValue().set(USER_SESSION + randomCookie,user,7,TimeUnit.DAYS);

        return ResultMap.getInstance()
                .put("xy365_3rd_session",randomCookie)
                .toMap();
    }

    @Override
    public Map<String, Object> login(String username, String password, String imageCode) {

        User user = userRepository.findUserByUsername(username);

        if(user == null) {
            throw new UserException(UserEnum.USER_NOT_ACCOUNT_FOUND);
        }

        // 加密后对比验证
        if(!user.getPassword().equals(DigestUtils.md5Hex(password))){
            throw new UserException(UserEnum.USER_PASSWORD_ERROR);
        }
        // 注册成功写入缓存 即为登录状态
        String randomCookie = DigestUtils.md5Hex(UUID.randomUUID().toString().replace("-",""));
        redisTemplate.opsForValue().set(USER_SESSION + randomCookie,user,2,TimeUnit.DAYS);

        return ResultMap.getInstance()
        .put("username",user.getUsername())
        .put("gender","女")
        .put("avatar",user.getAvatar())
        .put(USER_SESSION,randomCookie).toMap();
    }

    @Transactional
    @Override
    public Map<String, Object> register(UserForm userForm, String imageCode) {

        User existUser = userRepository.findUserByUsername(userForm.getUsername());

        if(existUser != null) {
            throw new UserException(UserEnum.USER_ACCOUNT_ALREADY);
        }

       User user = userRepository.save(
                User.builder()
                .username(userForm.getUsername())
                .mobile("1234567890")
                .password(DigestUtils.md5Hex(userForm.getPassword()))
                .gender("女")
                .avatar((xyProperties.getFileConfig().getImageServer()+"head_"+ RandomUtils.nextInt(1,10)+".png"))
                .status("0")
                .anonymousName(ChineseNameUtil.randomName())
                .nickname("无名英雄")
                .anonymousAvatar(xyProperties.getFileConfig().getImageServer()+"head_"+ RandomUtils.nextInt(1,10)+".png")
                .build()
       );

        // 注册成功写入缓存 即为登录状态
        String randomCookie = DigestUtils.md5Hex(UUID.randomUUID().toString().replace("-",""));
        redisTemplate.opsForValue().set(USER_SESSION + randomCookie,user,2,TimeUnit.DAYS);

        return ResultMap.getInstance()
                .put("username",user.getUsername())
                .put("nickname",user.getNickname())
                .put("userId",user.getId())
                .put("gender","女")
                .put("avatar",user.getAvatar())
                .put(USER_SESSION,randomCookie)
                .toMap();
    }


}
