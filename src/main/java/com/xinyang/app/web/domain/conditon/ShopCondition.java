package com.xinyang.app.web.domain.conditon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopCondition {

    /**
     * 查询模式
     * RECOMMEND 推荐
     * DEFAULT 默认
     * TOP 顶置
     *
     */
    private String model;

    /**
     * 类别码
     */
    private String categoryCode;

    /**
     *类型码
     */
    private String typeIds;

    public enum ModelEnum{
        RECOMMEND,DEFAULT,TOP;
    }

}
