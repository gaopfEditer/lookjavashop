package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("订单商品列表Vo")
public class OrderGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer goodsId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "SKU_ID")
    private Integer goodsSkuId;

    @ApiModelProperty(value = "SKU值名")
    private String goodsSkuValue;

    @ApiModelProperty(value = "商品原价")
    private BigDecimal goodsOriginalPrice;

    @ApiModelProperty(value = "购买价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "购买数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "商品总额")
    private BigDecimal goodsMoney;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty(value = "申请售后按钮")
    private Integer afterSalesBtn;

    @ApiModelProperty(value = "售后描述")
    private String afterSalesMsg;
    @ApiModelProperty(value = "是否依然在售后中")
    private Integer afterSalesStatus;
    @ApiModelProperty(value = "核销状态")
    private Integer verificationStatus;
    @ApiModelProperty(value = "核销状态字符串")
    private String verificationStatusStr;
    @ApiModelProperty(value = "核销时间")
    private String verificationTimeStr;


}
