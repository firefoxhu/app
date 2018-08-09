package com.xinyang.app.web.domain.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class CommentForm {

    @NotEmpty(message = "系统异常请刷新重试")
    private String articleId;

    @Size(min = 3,max = 128,message = "请多写2个字！")
    private String content;

    private String location;

    /**
     * 0正常
     * 1匿名
     */
    private String anonymous;


    private String nickname;

    private String avatar;

}
