package com.xinyang.app.web.service.impl;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Comment;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.repository.CommentRepository;
import com.xinyang.app.core.repository.UserRepository;
import com.xinyang.app.web.domain.dto.CommentDTO;
import com.xinyang.app.web.domain.form.CommentForm;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.CommentService;
import com.xinyang.app.web.util.DateUtil;
import com.xinyang.app.web.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;


    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private UserRepository userRepository;


    @Override
    public Map<String, Object> findComment(Long articleId,Pageable pageable) {


        Page<Comment> page = commentRepository.findAll((root, query, cb)->
                cb.and(
                        cb.equal(root.get("articleId"),articleId),
                        cb.and(cb.equal(root.get("status"),"0")
                )
            ),pageable);

        Map<String,Object> map = Maps.newHashMap();

        map.put("hasNext",page.hasNext());
        map.put("list",page.getContent().stream().map(e->
                CommentDTO.builder().
                        commentId(e.getId()).
                        avatar(
                                e.getAnonymous().equals("0") ? e.getUser().getAvatar() : e.getUser().getAnonymousAvatar()
                        ).
                        fabulous(e.getFabulous()).
                        content(e.getContent()).
                        nickname(
                                e.getAnonymous().equals("0") ? e.getUser().getNickname() : e.getUser().getAnonymousName()
                        ).
                        createTime(DateUtil.calculateTime(e.getCreateTime()))
                        .location(e.getLocation())
                        .build()
        ).collect(Collectors.toList()));
        map.put("commentsNumber",commentRepository.count((root,query,cb)->cb.and(cb.equal(root.get("articleId"),articleId))));

        return map;
    }

    @Transactional
    @Override
    public Map<String,Object> writeComment(HttpServletRequest request, CommentForm commentForm) {

        User user = Optional.ofNullable(
                redisTemplate.opsForValue().get(request.getHeader("xy365_3rd_session"))
        ).map(u->
                (User)u
        ).orElseThrow(()->
                new AuthException("纳秒之间的用户登录过期！万年一见。")
        );

        if(commentForm.getAnonymous().equals("0")){ // 更新用户的基本信息
            User user1 = userRepository.findById(user.getId()).get();
            userRepository.save(
                    User.builder().id(user1.getId())
                            .openId(user1.getOpenId())
                            .avatar(
                                    Optional.ofNullable(commentForm.getAvatar()).orElse(user1.getAvatar())
                            )
                            .nickname(
                                    Optional.ofNullable(commentForm.getNickname()).orElse(user1.getNickname())
                            )
                            .anonymousAvatar(
                                    user1.getAnonymousAvatar()
                            )
                            .anonymousName(
                                    user1.getAnonymousName()
                            )
                            .status(user1.getStatus())
                            .build()
            );
        }



        CommentDTO commentDTO = Optional.of(
                commentRepository.save(
                    Comment.builder().content(commentForm.getContent()).location(commentForm.getLocation()).user(
                            User.builder().id(user.getId()).build()
                    ).articleId(
                          Long.valueOf(commentForm.getArticleId())
                    ).location(
                            Optional.ofNullable(commentForm.getLocation()).orElse("信阳市（默认）")
                    ).anonymous(commentForm.getAnonymous())
                    .ip((IpUtil.getIpAddr(request))).build()
                )
        ).map(c -> CommentDTO.builder()
                            .location(c.getLocation())
                            .content(c.getContent())
                            .commentId(c.getId())
                            .fabulous(c.getFabulous())
                            .nickname(
                                    c.getAnonymous().equals("0") ? (user.getNickname()  == null ? "未授权" : user.getNickname()) : (user.getAnonymousName())
                            )
                            .avatar(
                                    c.getAnonymous().equals("0") ? (user.getAvatar() == null ? user.getAnonymousAvatar() : user.getNickname()) : user.getAnonymousAvatar()
                            )
                            .createTime(
                                    DateUtil.calculateTime(c.getCreateTime())
                            )
                            .build()
        ).orElseThrow(()->
                new RuntimeException("评论文章失败！")
        );

        Map<String,Object> map = Maps.newHashMap();

        map.put("data",commentDTO);
        // web socket 通知作者有评论消息查看

        return map;
    }

    @Override
    public Map<String, Object> findCommentById(Long commentId) {

        Map<String,Object> map = Maps.newHashMap();

        map.put("data",commentRepository.findById(commentId).map(e->
                CommentDTO.builder()
                        .content(e.getContent())
                        .nickname(e.getUser().getNickname())
                        .avatar(e.getUser().getAvatar())
                        .createTime(DateUtil.calculateTime(e.getCreateTime()))
                        .fabulous(e.getFabulous())
                        .location(e.getLocation() )
                        .build()

        ).orElseGet(CommentDTO::new));

        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> fabulousComment(Long commentId) {
        commentRepository.fabulousComment(commentId);
        Map<String,Object> map = Maps.newHashMap();
        map.put("message","更新成功！");
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> unfabulousComment(Long commentId) {
        commentRepository.unfabulousComment(commentId);
        Map<String,Object> map = Maps.newHashMap();
        map.put("message","更新成功！");
        return map;
    }


}
