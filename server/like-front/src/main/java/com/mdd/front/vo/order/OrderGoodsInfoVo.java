package com.mdd.front.vo.order;

import com.alibaba.fastjson2.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "订单商品Vo")
public class OrderGoodsInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Integer goodsId;

    @ApiModelProperty(value = "商品货号")
    private String goodsCode;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品类型")
    private Integer goodsType;

    @ApiModelProperty(value = "商品规格id")
    private Integer goodsSkuId;

    @ApiModelProperty(value = "商品规格值")
    private String skuValueArr;

    @ApiModelProperty(value = "规格图片")
    private String skuImage;

    @ApiModelProperty(value = "商品主图")
    private String image;

    @ApiModelProperty(value = "商品金额")
    private BigDecimal price;

    @ApiModelProperty(value = "商品原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "商品数量")
    private Integer num;

    @ApiModelProperty(value = "商品状态")
    private Integer status;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "体积")
    private Double volume;

    @ApiModelProperty(value = "运费类型:[1=包邮, 2=运费模板]")
    private Integer expressType;

    @ApiModelProperty(value = "运费")
    private BigDecimal expressMoney;

    @ApiModelProperty(value = "运费模板")
    private Integer expressTemplateId;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponMoney;

    @ApiModelProperty(value = "商品金额")
    private BigDecimal goodsAmount;

    @ApiModelProperty(value = "是否开启快递配送")
    private Integer isExpress;

    @ApiModelProperty(value = "是否开启上门自提")
    private Integer isSelffetch;

    @ApiModelProperty(value = "配送方式可买")
    private Boolean deliveryAble;

    @ApiModelProperty(value = "是否可以购买")
    private Boolean buyAble;
    @ApiModelProperty(value = "不能购买的原因")
    private JSONArray buyAbleMsg;
}
