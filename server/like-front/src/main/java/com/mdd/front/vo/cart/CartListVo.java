package com.mdd.front.vo.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "购物车Vo")
public class CartListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("购物车商品")
    private List<CartGoodsListVo> goods;

    @ApiModelProperty("购物车数量")
    private Integer totalNum;

    @ApiModelProperty("购物车价格")
    private BigDecimal totalPrice;






}
