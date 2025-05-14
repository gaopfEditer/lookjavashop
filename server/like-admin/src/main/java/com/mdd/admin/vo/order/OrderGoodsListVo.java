package com.mdd.admin.vo.order;

import com.mdd.admin.aop.Log;
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

    @ApiModelProperty(value = "SKU_ID")
    private Integer goodsSkuId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品货号")
    private String goodsCode;

    @ApiModelProperty(value = "SKU值名")
    private String goodsSkuValue;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "购买数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "商品原价")
    private BigDecimal goodsOriginalPrice;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal goodsPrice;

    @ApiModelProperty(value = "商品总额")
    private BigDecimal goodsMoney;

    @ApiModelProperty(value = "订单总额")
    private BigDecimal money;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal expressMoney;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponMoney;

    @ApiModelProperty(value = "售后状态: [1=无售后, 2=售后中, 3=售后完成]")
    private Integer afterSale;

    @ApiModelProperty(value = "售后状态描述")
    private String afterMsg;

    @ApiModelProperty(value = "核销时间")
    private Long verificationTime;
    @ApiModelProperty(value = "核销状态")
    private Integer verificationStatus;
}
