package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "售后商品详情Vo")
public class OrderAfterGoodsDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("售后商品ID")
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单商品id")
    private Integer orderGoodsId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品主图")
    private String goodsImage;

    @ApiModelProperty("商品规格")
    private String goodsSkuValue;

    @ApiModelProperty("商品单价")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品数量")
    private Integer goodsNum;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsMoney;

    @ApiModelProperty("商品应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty("商品实付金额")
    private BigDecimal payMoney;
}
