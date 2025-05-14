package com.mdd.front.validate.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 商品评价参数
 */
@Data
@ApiModel("商品评价参数")
public class GoodsCommentCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单商品id缺失")
    @ApiModelProperty(value = "订单商品ID", required = true)
    private Integer orderGoodsId;

    @ApiModelProperty(value = "商品评分")
    private Integer score;

    @ApiModelProperty(value = "评价图片")
    private List<String> images;

    @ApiModelProperty(value = "评价内容")
    private String content;

    @ApiModelProperty(value = "描述评分")
    private Integer describeScore;

    @ApiModelProperty(value = "服务评分")
    private Integer serviceScore;

    @ApiModelProperty(value = "物流评分")
    private Integer logisticsScore;
}
