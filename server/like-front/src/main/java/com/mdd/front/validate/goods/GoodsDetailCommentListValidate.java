package com.mdd.front.validate.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品详情页评价列表参数
 */
@Data
@ApiModel("商品详情页评价列表参数")
public class GoodsDetailCommentListValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品id参数丢失")
    @ApiModelProperty(value = "商品ID", required = true)
    private Integer goodsId;

    @ApiModelProperty(value = "是否有图: [0=无图， 1=有图]")
    private Integer hasImage;
}
