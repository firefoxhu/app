package com.xinyang.app.web.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryDTO {

    private String code;

    private String name;

    private String picture;

    private int sort;

    private List<TypeDTO> typeDTOS;

}
