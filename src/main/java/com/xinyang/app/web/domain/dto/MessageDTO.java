package com.xinyang.app.web.domain.dto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MessageDTO {

    private Long id;

    private Long fromUser;


    private Long toUser;


    private String content;

    private String isRead;

    private String dateTime;




}
