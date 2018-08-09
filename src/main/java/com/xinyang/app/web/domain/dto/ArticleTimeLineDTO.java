package com.xinyang.app.web.domain.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ArticleTimeLineDTO {

    private long articleId;

    private String timeLine;

}
