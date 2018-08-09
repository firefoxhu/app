package com.xinyang.app.web.service;
import com.xinyang.app.web.domain.form.CommentForm;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Service
public interface CommentService{

    Map<String,Object> findComment(Long articleId, Pageable pageable);

    Map<String,Object> writeComment(HttpServletRequest request, CommentForm commentForm);

    Map<String,Object> findCommentById(Long commentId);

    Map<String,Object> fabulousComment(Long commentId);

    Map<String,Object> unfabulousComment(Long commentId);

}
