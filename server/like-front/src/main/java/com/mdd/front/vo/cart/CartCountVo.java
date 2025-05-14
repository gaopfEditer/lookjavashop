package com.mdd.front.vo.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "购物车数量Vo")
public class CartCountVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("购物车数量")
    private Long num;






}
