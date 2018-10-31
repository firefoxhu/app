package com.xinyang.app.web.service.impl;
import com.xinyang.app.core.model.Fabulous;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.repository.FabulousRepository;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.FabulousService;
import com.xinyang.app.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class FabulousServiceImpl implements FabulousService {

    @Autowired
    private FabulousRepository fabulousRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Fabulous writeFabulous(HttpServletRequest request,Fabulous fabulous) {

        String xcxSession = request.getHeader("Third-Session");
        String appSession = request.getHeader("App-Session");
        User user;
        try {
            String redis_key = UserService.USER_SESSION + (xcxSession == null ? appSession : xcxSession);
            user = (User) redisTemplate.opsForValue().get(redis_key);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if(user == null) {
            throw new AuthException("业务缓存读取用户信息失败！");
        }

        return fabulousRepository.save(fabulous);
    }

    @Transactional
    @Override
    public void cancelFabulous(HttpServletRequest request,long fabulousId) {

        String xcxSession = request.getHeader("Third-Session");
        String appSession = request.getHeader("App-Session");
        User user;
        try {
            String redis_key = UserService.USER_SESSION + (xcxSession == null ? appSession : xcxSession);
            user = (User) redisTemplate.opsForValue().get(redis_key);
        }catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if(user == null) {
            throw new AuthException("业务缓存读取用户信息失败！");
        }


        fabulousRepository.deleteById(fabulousId);
    }

    @Override
    public List<Fabulous> findFabulousByArticleId(Pageable page,long articleId) {
        return fabulousRepository.findFabulousByArticleId(page,articleId);
    }
}
