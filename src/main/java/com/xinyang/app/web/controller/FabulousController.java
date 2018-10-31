package com.xinyang.app.web.controller;

import com.xinyang.app.core.model.Fabulous;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.service.FabulousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin("*")
@RequestMapping("/fabulous")
public class FabulousController {

    @Autowired
    private FabulousService fabulousService;


    @GetMapping
    public SimpleResponse list(@PageableDefault(page = 0,size = 20,direction = Sort.Direction.DESC) Pageable page,String articleId){
        return SimpleResponse.success(fabulousService.findFabulousByArticleId(page,Long.valueOf(articleId)));
    }


    @PutMapping
    public SimpleResponse addAuth(HttpServletRequest request,String articleId, String username){
        return SimpleResponse.success(fabulousService.writeFabulous(request,new Fabulous(null,Long.valueOf(articleId),username)));
    }


    @DeleteMapping
    public SimpleResponse deleteAuth(HttpServletRequest request,String fabulousId){
        try{
            fabulousService.cancelFabulous(request,Long.valueOf(fabulousId));
            return SimpleResponse.success();
        }catch (Exception e) {
            e.printStackTrace();
            return SimpleResponse.fail("失败");
        }

    }

}
