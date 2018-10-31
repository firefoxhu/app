package com.xinyang.app.web.domain.dto;

import com.xinyang.app.core.model.Fabulous;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class ArticleDTO {

    private Long articleId;

    private String author;

    private String avatar;

    private String top;

    private String content;

    private List<String> pictures;

    private String location;

    private long views;

    private long fabulous;

    private long commentsNumber;

    private String createTime;

    private List<CommentDTO> comments;

    private List<Fabulous> fabulousList;

}
