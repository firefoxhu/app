package com.xinyang.app.core.controller;
import com.xinyang.app.core.plugin.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/sms")
public class SMSController {


    @Autowired
    private SmsSender smsSender;

    @GetMapping("/send")
    public Map<String,Object> SmsSend(String phone){
        return   smsSender.send(phone);
    }


    @GetMapping("/verification")
    public Map<String,Object> SmsValidate(String phone,String code){
        return   smsSender.send(phone);
    }

}
