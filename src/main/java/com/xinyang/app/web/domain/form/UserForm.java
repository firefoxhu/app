package com.xinyang.app.web.domain.form;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserForm {

    private String nickname;

    private String phone;

    private String code;

    private String avatar;

    private String gender;

    private String thirdSession;

}
