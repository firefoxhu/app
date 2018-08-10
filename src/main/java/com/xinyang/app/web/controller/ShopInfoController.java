package com.xinyang.app.web.controller;
import com.xinyang.app.web.domain.form.ShopInfoForm;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.service.ShopInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin("*")
@RequestMapping("/shopInfo")
public class ShopInfoController{

    @Autowired
    private ShopInfoService shopInfoService;

    @PostMapping("/views")
    public DeferredResult<SimpleResponse> viewsShopInfo(Long shopInfoId){
        DeferredResult<SimpleResponse> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->shopInfoService.viewShop(shopInfoId))
                .whenCompleteAsync((result, throwable) -> deferredResult.setResult(SimpleResponse.success()));
        return deferredResult;
    }


    @PostMapping("/updateShopInfo")
    public SimpleResponse updateShopInfoAuth(HttpServletRequest request,@RequestBody ShopInfoForm shopInfoForm){
        try{
            return SimpleResponse.success(shopInfoService.updateShopInfo(request,shopInfoForm));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }



}
