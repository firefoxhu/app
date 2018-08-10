package com.xinyang.app.web.controller;
import com.xinyang.app.web.domain.form.CommentForm;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin("*")
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public SimpleResponse findCommentByArticleId(@PageableDefault(page = 0,size = 8,sort = {"createTime","fabulous"},direction = Sort.Direction.DESC) Pageable page, Long articleId){
        try{
            return SimpleResponse.success(commentService.findComment(articleId,page));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @PostMapping("/write")
    public SimpleResponse writeCommentAuth(HttpServletRequest request, @RequestBody @Valid CommentForm commentForm){
        try{
            return SimpleResponse.success(commentService.writeComment(request,commentForm));
        }catch (AuthException e){
            e.printStackTrace();
            return SimpleResponse.error(e.getCode(),e.getMessage());
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @PostMapping("/fabulous")
    public DeferredResult<SimpleResponse> fabulousComment(Long commentId){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->commentService.fabulousComment(commentId))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(SimpleResponse.success()));
        return deferredResult;
    }

    @PostMapping("/unfabulous")
    public DeferredResult<SimpleResponse> unfabulousComment(Long commentId){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->commentService.unfabulousComment(commentId))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(SimpleResponse.success()));
        return deferredResult;
    }

}
