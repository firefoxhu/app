package com.xinyang.app.web.controller;
import com.xinyang.app.web.domain.conditon.ShopCondition;
import com.xinyang.app.web.domain.support.SimpleResponse;
import com.xinyang.app.web.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;


    // TODO get请求无需要requestbody  post需要
    @GetMapping("/list")
    public SimpleResponse findShop(@PageableDefault(page = 0,size = 8,sort = {"topPlacement","recommend","createTime"},direction = Sort.Direction.DESC) Pageable page, ShopCondition shopCondition){
        try {
            return SimpleResponse.success(shopService.findShopByCondition(shopCondition, page));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @GetMapping("/shopInfo")
    public SimpleResponse findShopInfoById(Long shopInfoId){
        try {
            return SimpleResponse.success(shopService.findShopByShopInfoId(shopInfoId));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }

    @GetMapping("/shopInfo/type")
    public SimpleResponse findShopByType(@PageableDefault(page = 0,size = 8,sort = {"id"},direction = Sort.Direction.DESC) Pageable page,Long typeId){
        try {
            return SimpleResponse.success(shopService.findShopByType(typeId,page));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }


    @GetMapping("/checkName")
    public SimpleResponse checkShopExist(String shopName){
        try {
            return SimpleResponse.success(shopService.checkShopNameExist(shopName));
        }catch (Exception e){
            return SimpleResponse.error(e.getMessage());
        }
    }



}
