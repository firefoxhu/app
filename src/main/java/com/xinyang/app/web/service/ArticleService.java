package com.xinyang.app.web.service;
import com.xinyang.app.web.domain.form.ArticleForm;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ArticleService{

   Map<String,Object> findArticle(Pageable pageable);

   Map<String,Object> listOwnerTimeLine(HttpServletRequest request, Pageable pageable);

   Map<String,Object>   writeArticle(HttpServletRequest request, ArticleForm articleForm);

   Map<String,Object> findArticleById(long articleId);

   Map<String,Object> viewsArticle(String articleId);

   Map<String,Object> fabulousArticle(String articleId);

   Map<String,Object> unfabulousArticle(String articleId);

   Map<String,Object> countArticleByUser(HttpServletRequest request);

   Map<String,Object> removeByArticleId(HttpServletRequest request, long articleId);


}
