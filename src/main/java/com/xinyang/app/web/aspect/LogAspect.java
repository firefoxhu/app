package com.xinyang.app.web.aspect;
import com.xinyang.app.core.model.User;
import com.xinyang.app.web.exception.AuthException;
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

        String session = request.getHeader("Third-Session");
        log.info("Third-Session",session);
        //判断当前方法是否需要权限验证
        if(methodName.contains("Auth")){
            log.info("需要进行权限登录认证！");
            Optional.ofNullable(
                    redisTemplate.opsForValue().get(
                            session == null ? "" : session
                    )
            ).map(u->{
                log.info("认证成功当前用户：{}",u);
                return  (User)u;
            }).orElseThrow(()->{
                log.info("该用户未登录 认证失败！");
                return new AuthException("请登录小程序！");
            });

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
            log.error("操作异常结果：{}",throwable.getMessage());
            new RuntimeException(throwable.getMessage());
        }
        log.info(">>返回的结果：{}","OK");
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>系统拦截请求关闭<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        return result;
    }






}
