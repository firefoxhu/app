package com.xinyang.app.web.domain.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class ArticleForm {


    private Long articleId;

    @NotNull(message="您还是写点什么吧！")
    @Max(value = 256,message = "您是个有故事的人，可系统限制我只能让您输入小于256个字，我很抱歉！ ")
    @Min(value = 6,message = "您输入字太少了，语言是打开心灵的大门，在多说点吧！")
    private String content;

    @Max(value = 32,message = "您这个定位是火星的，请换一个吧！ ")
    private String location;

    /**
     * 0 微信名称
     * 1 匿名名称
     */
    private String  anonymous;

    private String images;


    private String nickname;

    private String avatar;

}
