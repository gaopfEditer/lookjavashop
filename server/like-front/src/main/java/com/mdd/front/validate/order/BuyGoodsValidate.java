package com.mdd.front.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("订单商品参数")
public class BuyGoodsValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品ID")
    private Integer goodsId;

    @ApiModelProperty(value = "商品规格ID")
    private Integer skuId;

    @ApiModelProperty(value = "商品数量")
    private Integer num;

    @ApiModelProperty(value = "购物车id(购物车下单必填)")
    private Integer cartId;

    @ApiModelProperty(value = "秒杀活动ID")
    private Integer seckillId;

    @ApiModelProperty(value = "是否在配送范围")
    private Boolean deliveryAble;

}
