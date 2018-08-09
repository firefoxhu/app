package com.xinyang.app.web.exception.hanlder;
import com.xinyang.app.web.domain.support.SimpleResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.security.auth.message.AuthException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{


    /**
     * 用户认证异常
     */
    @ExceptionHandler(AuthException.class)
    public SimpleResponse authExceptionException(Exception e) {
        return SimpleResponse.error("用户认证异常");
    }


    /**
     *校验一些参数不匹配啊，Get post方法不对啊之类的 具有状态吗的
     */
    @Override
    protected ResponseEntity handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity(SimpleResponse.error("校验一些参数不匹配啊，Get post方法不对支持等"), status);
    }

    /**
     * 服务器通用异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public SimpleResponse systemException(Exception e){
        return SimpleResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),"服务运行异常");
    }

}
