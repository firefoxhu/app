package com.xinyang.app.web.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TypeDTO {

    private long typeId;

    private String categoryCode;

    private String name;

    private int sort;

    private String picture;
}
