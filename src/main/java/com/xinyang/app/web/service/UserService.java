package com.xinyang.app.web.service;
import com.xinyang.app.web.wx.WxSession;

import java.util.Map;

public interface UserService{

    Map<String,Object>  registerOrLogin(WxSession wxSession) throws Exception;

}
