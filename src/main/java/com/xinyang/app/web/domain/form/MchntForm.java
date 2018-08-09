package com.xinyang.app.web.domain.form;

import lombok.Data;

@Data
public class MchntForm {

    // mchnt
    private String mchntName;

    private String mchntPhone;

    private String smsCode;

    //shop
    private String shopName;

    private String address;

    private String addressDescription;

    private String latitude;

    private String longitude;

    //shopinfo

    private String categoryCode;

    private String shopPhone;

    private String skillDescription;

    private String bannerPicture;

    private String description;

}
