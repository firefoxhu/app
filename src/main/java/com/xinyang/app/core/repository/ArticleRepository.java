package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.Article;
import com.xinyang.app.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends XyRepository<Article>{

    @Modifying
    @Query(value = "update xy_article set views = views + 1 where id= :id",nativeQuery = true)
    int updateViewsCount(@Param("id") long id);

    @Modifying
    @Query(value = "update xy_article set fabulous = fabulous + 1 where id= :id",nativeQuery = true)
    int fabulousArticle(@Param("id") long id);

    @Modifying
    @Query(value = "update xy_article set fabulous = fabulous - 1 where id= :id",nativeQuery = true)
    int unfabulousArticle(@Param("id") long id);

    @Modifying
    @Query(value = "update xy_article set status = :status where id= :id",nativeQuery = true)
    int updateStatus(@Param("status") String status, @Param("id") long id);

    long  countArticleByUser(User user);

    Page<Article> findArticleByUserAndStatus(Pageable pageable, User user, String status);


    Article findArticleByUserAndStatusAndId(User user, String status, Long articleId);
}
