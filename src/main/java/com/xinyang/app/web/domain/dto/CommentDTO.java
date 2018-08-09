package com.xinyang.app.web.domain.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long commentId;

    private String avatar;

    private String nickname;

    private String content;

    private int fabulous;

    private String location;

    private String createTime;


}
