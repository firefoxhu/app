package com.xinyang.app.web.service.impl;

import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Article;
import com.xinyang.app.core.model.User;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.ArticleRepository;
import com.xinyang.app.core.repository.CommentRepository;
import com.xinyang.app.core.repository.UserRepository;
import com.xinyang.app.core.util.FileUtil;
import com.xinyang.app.web.domain.dto.ArticleDTO;
import com.xinyang.app.web.domain.form.ArticleForm;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.ArticleService;
import com.xinyang.app.web.service.MessageService;
import com.xinyang.app.web.util.DateUtil;
import com.xinyang.app.web.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private XyProperties xyProperties;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private MessageService messageService;


    @Override
    public Map<String, Object> findArticle(Pageable pageable) {

        Page<Article> page = articleRepository.findAll((root, query, cb)-> cb.and(cb.equal(root.get("status"),"0")),pageable);

        Map<String,Object> map = Maps.newHashMap();
        map.put("hasNext",page.hasNext());
        map.put("list",page.getContent().parallelStream().map(o->
                                    ArticleDTO.builder().articleId(o.getId())
                                            .avatar(
                                                    o.getAnonymous().equals("1") ? o.getUser().getAnonymousAvatar() : o.getUser().getAvatar()
                                            ).author(
                                                    o.getAnonymous().equals("1") ? o.getUser().getAnonymousName() : o.getUser().getNickname()
                                            )
                                            .top(o.getTop())
                                            .views(o.getViews())
                                            .content(o.getContent())
                                            .commentsNumber(o.getCommentsNumber())
                                            .location(o.getLocation())
                                            .createTime(DateUtil.calculateTime(o.getCreateTime()))
                                            .fabulous(o.getFabulous())
                                    .pictures(
                                            Optional.ofNullable(o.getPictures())
                                                    .map(
                                                            picture-> Arrays.asList(picture.split(",")).stream().map(p->
                                                                    xyProperties.getFileConfig().getImageServer() + p).collect(Collectors.toList()
                                                            )
                                                    ).orElse(null)
                                    )
                                            //最好放在最后位置处理 否则会出现无效的问题
                                    .commentsNumber(
                                            commentRepository.count((root,query,cb)->cb.equal(root.get("articleId"),o.getId()))
                                    )
                                   .build()
        ).collect(Collectors.toList()));
        return map;
    }

    @Override
    public Map<String, Object> listOwnerTimeLine(HttpServletRequest request,Pageable pageable) {

        User user = Optional.ofNullable(redisTemplate.opsForValue().get(request.getHeader("xy365_3rd_session"))).map(u->(User)u ).orElseThrow(()->new AuthException("纳秒之间的用户登录过期！万年一见。"));

        Page<Article> page = articleRepository.findArticleByUserAndStatus(pageable,user,"0");

        return ResultMap.getInstance().put("hasNext",page.hasNext())
                .put("list",
                        page.getContent().stream().map(o->
                                ArticleDTO.builder().articleId(o.getId())
                                        .avatar(
                                                o.getAnonymous().equals("1") ? o.getUser().getAnonymousAvatar() : o.getUser().getAvatar()
                                        ).author(
                                        o.getAnonymous().equals("1") ? o.getUser().getAnonymousName() : o.getUser().getNickname()
                                )
                                        .top(o.getTop())
                                        .views(o.getViews())
                                        .content(o.getContent())
                                        .commentsNumber(o.getCommentsNumber())
                                        .location(o.getLocation())
                                        .createTime(DateUtil.formatDateTime(o.getCreateTime()))
                                        .fabulous(o.getFabulous())
                                        .pictures(
                                                Optional.ofNullable(o.getPictures())
                                                        .map(
                                                                picture-> Arrays.asList(picture.split(",")).stream().map(p->
                                                                        xyProperties.getFileConfig().getImageServer() + p).collect(Collectors.toList()
                                                                )
                                                        ).orElse(null)
                                        )
                                        .build()
                        ).collect(Collectors.toList())
                ).toMap();
    }

    @Transactional
    @Override
    public  Map<String,Object> writeArticle(HttpServletRequest request, ArticleForm articleForm) {

        User user = Optional.ofNullable(redisTemplate.opsForValue().get(request.getHeader("xy365_3rd_session"))).map(u->(User)u).orElseThrow(()->new AuthException("纳秒之间的用户登录过期！万年一见。"));


        if(articleForm.getAnonymous().equals("0")){ // 更新用户的基本信息
            User user1 = userRepository.findById(user.getId()).get();
           userRepository.save(
                   User.builder().id(user1.getId())
                           .openId(user1.getOpenId())
                           .avatar(
                               Optional.ofNullable(articleForm.getAvatar()).orElse(user1.getAvatar())
                           )
                           .nickname(
                                   Optional.ofNullable(articleForm.getNickname()).orElse(user1.getNickname())
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

        // 图片处理
        if(articleForm.getImages() != null){
            // 生成本地 缩略图 和 压缩图
           Arrays.asList(articleForm.getImages().split(",")).parallelStream().forEach(i->
                    FileUtil.localUpload(xyProperties.getFileConfig().getTempDir(),i)
           );

           // 将缩略图 和 压缩图上传七牛云
            Arrays.asList(articleForm.getImages().split(",")).parallelStream().forEach(i->
                    FileUtil.qiNiuUpload(xyProperties.getFileConfig().getTempDir(),i)
            );
        }

        articleRepository.save(
                Article.builder()
                        .content(articleForm.getContent())
                        .pictures(
                                Optional.ofNullable(articleForm.getImages()).orElse(null)
                        )
                        .anonymous(articleForm.getAnonymous())
                        .location(articleForm.getLocation())
                        .ip(IpUtil.getIpAddr(request))
                        .user(
                                User.builder().id(user.getId()).build()
                        )
                .build()
        );

        messageService.notice(user.getId(),"【系统消息】您发表的说说系统已经处理通过！");

        Map<String,Object> map = Maps.newHashMap();
        map.put("message","并发异步处理完毕");
        return map;

    }


    @Override
    public Map<String, Object> findArticleById(long articleId) {


        Article article = articleRepository.findById(articleId).orElseThrow(()->new RuntimeException("该文章可能已经删除了！"));

        Map<String,Object> map = Maps.newHashMap();

        map.put("list",ArticleDTO
                .builder()
                .articleId(article.getId())
                .avatar(article.getUser().getAvatar())
                .author(article.getUser().getNickname())
                .top(article.getTop())
                .views(article.getViews())
                .commentsNumber(article.getCommentsNumber())
                .content(article.getContent())
                .pictures(
                        Optional
                                .ofNullable(article.getPictures())
                                .map(
                                        picture-> Arrays.asList(picture.split(",")).stream().map(p->
                                                xyProperties.getFileConfig().getImageServer() + p).collect(Collectors.toList()
                                        )
                                )
                                .orElse(null)
                )
                .location(article.getLocation())
                .createTime(DateUtil.calculateTime(article.getCreateTime()))
                .build()
        );
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> viewsArticle(String articleId) {

        articleRepository.updateViewsCount(Long.parseLong(articleId));
        Map<String,Object> map = Maps.newHashMap();
        map.put("message","更新成功！");

        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> fabulousArticle(String articleId) {
        articleRepository.fabulousArticle(Long.parseLong(articleId));
        Map<String,Object> map = Maps.newHashMap();
        map.put("message","更新成功！");
        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> unfabulousArticle(String articleId) {
        articleRepository.unfabulousArticle(Long.parseLong(articleId));
        Map<String,Object> map = Maps.newHashMap();
        map.put("message","更新成功！");
        return map;
    }

    @Override
    public Map<String, Object> countArticleByUser(HttpServletRequest request) {
        User user = Optional.ofNullable( redisTemplate.opsForValue().get(request.getHeader("xy365_3rd_session"))).map(u->(User)u).orElseThrow(()-> new AuthException("纳秒之间的用户登录过期！万年一见。"));
        return ResultMap.getInstance().put("countArticle",
                articleRepository.countArticleByUser(user)
        ).toMap();

    }

    @Transactional
    @Override
    public Map<String, Object> removeByArticleId(HttpServletRequest request, long articleId) {

        User user = Optional.ofNullable( redisTemplate.opsForValue().get(request.getHeader("xy365_3rd_session"))).map(u->(User)u).orElseThrow(()-> new AuthException("纳秒之间的用户登录过期！万年一见。"));

        Optional.ofNullable(articleRepository.findArticleByUserAndStatusAndId(user,"0",articleId)).map(article ->
                articleRepository.updateStatus("1",articleId)
        ).orElseThrow(()->new RuntimeException("删除的文章可已经被删除过！"));


        return ResultMap.getInstance().put("message","删除成功！").toMap();
    }


}
