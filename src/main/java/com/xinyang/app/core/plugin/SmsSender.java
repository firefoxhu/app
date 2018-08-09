package com.xinyang.app.core.plugin;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SmsSender {

    @Autowired
    private RedisTemplate redisTemplate;

    public Map<String,Object> send(String mobile) {

        Object times = redisTemplate.opsForValue().get(mobile+"Times");

        Map<String,Object> map = Maps.newHashMap();

        if(times == null || (int)times <= 3){
            // 随机验证码
            // String code = RandomStringUtils.randomNumeric(6);
            String code = "666666";
            // 保存验证码到缓存一段过期时间
            redisTemplate.opsForValue().set(mobile,code,30, TimeUnit.MINUTES);

            // 更新24小时内
            redisTemplate.opsForValue().set(mobile+"Times",times == null ? 1 : (int)times+1,24,TimeUnit.HOURS);

            log.info("当前手机：{} 验证码：{}",mobile,code);
            map.put("code",0);
            map.put("message","验证码短信已下发，请注意查收！");
        }else {
            map.put("code",-1);
            map.put("message","验证已超出当日的发送次数24小时后重试！");
        }
        return map;
    }

    public Map<String,Object> validate(String mobile,String code){
        Object  phone = redisTemplate.opsForValue().get(mobile);
        Map<String,Object> map = Maps.newHashMap();
        if(!org.springframework.util.StringUtils.isEmpty(phone)){
            if(StringUtils.equals(String.valueOf(phone),code)){
                redisTemplate.delete(mobile);
                map.put("message","验证成功！");
                map.put("status",true);
                return map;
            }else {
                map.put("message","验证码输入有误，请尝试从新输入！");
                map.put("status",false);
                return map;
            }
        }
        map.put("status",false);
        map.put("message","验证码已过期，或已被使用！");
        return map;

    }

}
