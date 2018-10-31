package com.xinyang.app.web.aspect;
import com.xinyang.app.core.model.User;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.UserService;
import com.xinyang.app.web.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import nl.bitwalker.useragentutils.UserAgent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private static final String SESSION_KEY = UserService.USER_SESSION;

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("execution(* com.xinyang.app.web.controller.*.*(..))")
    public void log(){}

    @Around("log()")
    public Object interceptorLog(ProceedingJoinPoint pjp){
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>系统拦截请求日志开启<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String methodName = pjp.getSignature().getName();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Enumeration enum1 = request.getHeaderNames();
        log.debug("-----------header----------------->");
        while(enum1.hasMoreElements()){
            String key = (String)enum1.nextElement();
            String value = request.getHeader(key);
            log.info(key + ":" + value);
        }

        //判断当前方法是否需要权限验证
        if(methodName.contains("Auth")){

            log.info("需要进行权限登录认证！");

            String xcxSession = request.getHeader("Third-Session");

            String appSession = request.getHeader("App-Session");

            String session_key = xcxSession == null ? appSession : xcxSession;

            if(xcxSession == null && appSession == null) {
                log.error("需要认证的没有传入【小程序认证头：Third-Session App认证头：App-Session】");
                throw new RuntimeException("需要认证的没有传入【小程序认证头：Third-Session App认证头：App-Session】");
            }

            String redis_key = SESSION_KEY + session_key;

            Object redisCache = redisTemplate.opsForValue().get(redis_key);

            if(redisCache == null) {
                throw new AuthException("请登录小程序！");
            }

        }
        log.info("客户端IP地址：{}", IpUtil.getIpAddr(request));
        log.info(">>请求的控制器名称：{}", pjp.getTarget().getClass().getName());
        log.info(">>请求方法名称：{}",methodName);
        log.info(">>请求的参数：{}",pjp.getArgs());
        log.info(">>========================操作系统信息======================================");
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        log.info(">>系统的ID:{}",userAgent.getId());
        log.info(">>系统设备：{}",userAgent.getBrowser());
        log.info(">>操作系统：{}",userAgent.getOperatingSystem());
        log.info(">>系统版本：{}",userAgent.getBrowserVersion());
        log.info(">>当前的系统信息：{}",userAgent.toString());
        log.info(">>========================操作最终结果======================================");
        Object result =null;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error("操作异常结果：{}",throwable.getMessage());
            new RuntimeException(throwable.getMessage());
        }
        log.info(">>返回的结果：{}",result);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>系统拦截请求关闭<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return result;
    }






}
