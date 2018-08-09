package com.xinyang.app.web.service;

import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface MessageService {


    Map<String,Object> messageList(HttpServletRequest request, Pageable pageable);

    Map<String,Object> updateReaded(long messageId);

    Map<String,Object> unReaderCount(HttpServletRequest request);

    Map<String,Object> notice(long toUser, String message);

    Map<String,Object> remove(HttpServletRequest request, long messageId);

}
