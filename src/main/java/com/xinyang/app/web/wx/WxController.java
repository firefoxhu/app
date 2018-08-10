package com.xinyang.app.web.wx;

import com.alibaba.fastjson.JSON;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/wx")
@Slf4j
public class WxController {

    @Autowired
    private XyProperties xyProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public SimpleResponse login(String login_code, @RequestParam(required = false) String third_session){
        String url="https://api.weixin.qq.com/sns/jscode2session?appid="+xyProperties.getWxConfig().getAppId()+"&secret="+xyProperties.getWxConfig().getSecret()+"&js_code="+login_code+"&grant_type=authorization_code";
        try {
            URL weChatUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = weChatUrl.openConnection();
            // 设置通用的请求属性
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            // 建立实际的连接
            connection.connect();

            StringBuffer accessToken = new StringBuffer();
            try(
                    Reader in = new InputStreamReader(connection.getInputStream());
                    BufferedReader br =new BufferedReader(in)
            ){
                String line;
                while ((line = br.readLine()) != null) {
                    accessToken.append(line);
                }
            }

            // 获取openid
            WxSession wxSession = JSON.parseObject(accessToken.toString(),WxSession.class);
            wxSession.setExpired_session(third_session);
            return SimpleResponse.success(userService.registerOrLogin(wxSession));


            /*

            // 生成成3rd_session
            String[] cmdA = { "/bin/sh", "-c", "head -n 80 /dev/urandom | tr -dc A-Za-z0-9 | head -c 168" };

            try {

                Process process = Runtime.getRuntime().exec(cmdA);

                StringBuffer thirdSession = new StringBuffer();
                try(
                        Reader in = new InputStreamReader(process.getInputStream());
                        LineNumberReader br = new LineNumberReader(in)
                ){
                    String line;
                    while ((line = br.readLine()) != null) {
                        thirdSession.append(line).append("\n");
                    }

                }

                redisTemplate.opsForValue().set(thirdSession.toString(),"");

            } catch (IOException e) {
                e.printStackTrace();
            }

*/

        }catch (Exception e){
            e.printStackTrace();
        }
        return SimpleResponse.fail("error");
    }


    @GetMapping("/check_session")
    public SimpleResponse check_session(String third_session){
        Optional.ofNullable(
                redisTemplate.opsForValue().get(third_session)
        ).map(u->(User)u).orElseThrow(()-> new AuthException("系统检查登录状态异常！"));
        return SimpleResponse.success("OK");
    }
}
