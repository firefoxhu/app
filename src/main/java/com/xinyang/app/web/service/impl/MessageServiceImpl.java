package com.xinyang.app.web.service.impl;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Message;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.repository.MessageRepository;
import com.xinyang.app.web.domain.dto.MessageDTO;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.MessageService;
import com.xinyang.app.web.service.UserService;
import com.xinyang.app.web.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Map<String, Object> messageList(HttpServletRequest request, Pageable pageable) {
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
            throw new AuthException("业务缓存读取用户信息失败【需要重新登录】！");
        }

        Page<Message> messages = messageRepository.findAll((root, query, cb)->cb.and((Predicate[])Arrays.asList(
                cb.and(cb.equal(root.get("toUser"), user.getId())),
                cb.and(cb.equal(root.get("status"),"0"))
        ).toArray()),pageable);

        return ResultMap.getInstance().put("hasNext",messages.hasNext()).put("list",
                messages.getContent().stream().map(e-> MessageDTO.builder().id(e.getId()).fromUser(e.getFromUser()).toUser(e.getToUser()).content(e.getContent()).isRead(e.getIsRead()).dateTime(DateUtil.formatDateTime(e.getCreateTime())).build())
                        .collect(Collectors.toList())
        ).toMap();
    }
    @Transactional
    @Override
    public Map<String, Object> updateReaded(long messageId) {

        Message message = messageRepository.findById(messageId).orElseThrow(()->new RuntimeException("消息不存在！"));
        message.setIsRead("0");
        messageRepository.save(
                message
        );

        return ResultMap.getInstance().put("message","消息阅读完毕！").toMap();
    }

    @Override
    public Map<String, Object> unReaderCount(HttpServletRequest request) {
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
            throw new AuthException("业务缓存读取用户信息失败【需要重新登录】！");
        }
        return ResultMap.getInstance().put("unReadCount",
                messageRepository.count((root,query,cb)->
                        cb.and((Predicate[])Arrays.asList(
                                cb.and(cb.equal(root.get("toUser"), user.getId())),
                                cb.and(cb.equal(root.get("status"),"0")),
                                cb.and(cb.equal(root.get("isRead"),"1"))
                        ).toArray())
                )
        ).toMap();
    }

    @Override
    public Map<String, Object> notice(long toUser,String message) {
        messageRepository.save(
                Message.builder()
                        .fromUser(123456l)
                        .toUser(toUser)
                        .content(message)
                        .build()
        );
        Map<String,Object> map = Maps.newHashMap();
        map.put("message","OK");
        return map;
    }
    @Transactional
    @Override
    public Map<String, Object> remove(HttpServletRequest request,long messageId) {

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
            throw new AuthException("业务缓存读取用户信息失败【需要重新登录】！");
        }

        Message message = messageRepository.findById(messageId).orElseThrow(()->new RuntimeException("删除失败！消息不存在"));
        // user.getId() == message.getToUser()  X  long类型的不能用 == 比较  最好转为字符串
        if(String.valueOf(user.getId()).equals(String.valueOf(message.getToUser()))) {
            messageRepository.delete(
                    message
            );

            return ResultMap.getInstance().put("message","消息删除成功！").toMap();
        }

        return ResultMap.getInstance().put("message","越权删除失败！").toMap();
    }
}
