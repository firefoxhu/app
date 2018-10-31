package com.xinyang.app.web.service;
import com.xinyang.app.web.domain.form.UserForm;
import com.xinyang.app.web.login.wx.WxSession;

import java.util.Map;

public interface UserService{

    String USER_SESSION = "user_session_";

    /**
     * 小程序使用的微信登录
     * @param wxSession
     * @return
     * @throws Exception
     */
    Map<String,Object>  registerOrLogin(WxSession wxSession) throws Exception;

    /**
     * 登录
     * @param username
     * @param password
     * @param imageCode
     * @return
     */
    Map<String,Object>  login(String username,String password,String imageCode);


    /**
     * 注册
     * @param userForm
     * @param imageCode
     * @return
     */
    Map<String,Object> register(UserForm userForm,String imageCode);




}
