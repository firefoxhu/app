package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.Comment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends XyRepository<Comment>{

    @Modifying
    @Query(value = "update xy_comment set fabulous = fabulous + 1 where id= :id",nativeQuery = true)
    int fabulousComment(@Param("id") long id);

    @Modifying
    @Query(value = "update xy_comment set fabulous = fabulous - 1 where id= :id",nativeQuery = true)
    int unfabulousComment(@Param("id") long id);

}
