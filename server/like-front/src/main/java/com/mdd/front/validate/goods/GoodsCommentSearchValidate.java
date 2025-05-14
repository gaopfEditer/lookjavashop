package com.mdd.front.validate.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品评价列表搜索参数
 */
@Data
@ApiModel("商品评价列表搜索参数")
public class GoodsCommentSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单商品id缺失")
    @ApiModelProperty(value = "订单商品ID", required = true)
    private Integer isComment;

    @ApiModelProperty(value = "商品评分")
    private Integer score;
}
