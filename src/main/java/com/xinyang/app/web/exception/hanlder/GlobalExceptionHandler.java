package com.xinyang.app.web.exception.hanlder;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler{

    /**
     * 服务器通用异常
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SimpleResponse systemException(HttpServletRequest req, Exception e){

        if(e instanceof AuthException){
            return SimpleResponse.error(((AuthException) e).getCode(),e.getMessage());
        }

        return SimpleResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
    }

}
