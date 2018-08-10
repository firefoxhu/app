package com.xinyang.app.web.controller;
import com.xinyang.app.web.domain.form.ArticleForm;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.exception.AuthException;
import com.xinyang.app.web.service.ArticleService;
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
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/list")
    public SimpleResponse findArticle(@PageableDefault(page = 0,size = 8,sort = {"top","createTime"},direction = Sort.Direction.DESC) Pageable page){
       return SimpleResponse.success(articleService.findArticle(page));
    }

    @GetMapping("/listOwner")
    public SimpleResponse listOwnerAuth(HttpServletRequest request,@PageableDefault(page = 0,size = 8,sort = {"createTime"},direction = Sort.Direction.DESC) Pageable page){
        return SimpleResponse.success(articleService.listOwnerTimeLine(request, page));
    }

    @GetMapping
    public SimpleResponse findArticleById(Long id){
        return SimpleResponse.success(articleService.findArticleById(id));
    }

    @PostMapping("/write")
    public DeferredResult<SimpleResponse> writeArticleAuth(HttpServletRequest request, @RequestBody @Valid ArticleForm articleForm, BindingResult bindingResult){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();

        CompletableFuture.supplyAsync(()->
                articleService.writeArticle(request,articleForm)
        ).whenCompleteAsync((result, throwable) ->
                deferredResult.setResult(SimpleResponse.success())
        );
        return deferredResult;
    }

    @PostMapping("/views")
    public DeferredResult<SimpleResponse> viewsArticle(String articleId){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->articleService.viewsArticle(articleId))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(SimpleResponse.success()));
       return deferredResult;
    }

    @PostMapping("/fabulous")
    public DeferredResult<SimpleResponse> fabulousArticle(String articleId){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->articleService.fabulousArticle(articleId))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(SimpleResponse.success()));
        return deferredResult;
    }

    @PostMapping("/unfabulous")
    public DeferredResult<SimpleResponse> unfabulousArticle(String articleId){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->articleService.unfabulousArticle(articleId))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(SimpleResponse.success()));
        return deferredResult;
    }


    @GetMapping("/countArticle")
    public SimpleResponse countArticleByUserAuth(HttpServletRequest request){
        try{
            return SimpleResponse.success(articleService.countArticleByUser(request));
        }catch (AuthException e){
            return SimpleResponse.error(e.getCode(),e.getMessage());
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }


    @PostMapping("/remove")
    public SimpleResponse removeAuth(HttpServletRequest request,@RequestBody ArticleForm articleForm){
       try {
           return SimpleResponse.success(articleService.removeByArticleId(request,articleForm.getArticleId()));
       }catch (AuthException e){
           return SimpleResponse.error(e.getCode(),e.getMessage());
       }catch (Exception e){
           return SimpleResponse.error(e.getMessage());
       }
    }

}
