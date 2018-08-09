package com.xinyang.app.web.domain.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ShopDTO {

    private long shopId;

    private long shopInfoId;

    private List<String> bannerPicture;

    private String shopName;

    private String shopSkillDescription;

    private String address;

    private String description;

    private List<String> galleryPicture;

    private int views;

    private String latitude;

    private String longitude;

    private String shopPhone;

    private String mchntPhone;

    private String mchntName;

    private String shopCategoryName;

    private String shopCategoryCode;

    private List<TypeDTO> typeDTOS;





}
