package com.xinyang.app.web.login.app;
import com.xinyang.app.web.domain.form.UserForm;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.exception.UserException;
import com.xinyang.app.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/app")
@Slf4j
public class AppLoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录前需要删除缓存中的session
     * @return
     */
    @PostMapping("/login")
    public SimpleResponse login(String username,String password,String session_key){
        try{
            return SimpleResponse.success(userService.login(username, password, session_key));
        }catch (UserException u) {
            return SimpleResponse.error(u.getCode(),u.getMessage());
        }
    }

    @PostMapping("/register")
    public SimpleResponse register(@RequestBody UserForm userForm){
        try{
             return SimpleResponse.success(userService.register(userForm,""));
        }catch (UserException u) {
            return SimpleResponse.fail(u.getCode(),u.getMessage());
        }
    }






}
