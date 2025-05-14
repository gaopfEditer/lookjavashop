package com.mdd.admin.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("订单信息Vo")
public class OrderInfoListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户编号")
    private String orderSn;

    @ApiModelProperty("用户编号")
    private String userSn;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "下单用户")
    private String nickname;

    @ApiModelProperty(value = "商品ID")
    private Integer goodsId;

    @ApiModelProperty(value = "SKU_ID")
    private Integer goodsSkuId;

    @ApiModelProperty(value = "SKU值名")
    private String goodsSkuValue;

    @ApiModelProperty(value = "单品原价")
    private BigDecimal goodsOriginalPrice;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal goodsPrice;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "购买数量")
    private Integer num;

    @ApiModelProperty(value = "订单总额")
    private BigDecimal money;

    @ApiModelProperty(value = "商品总额")
    private BigDecimal goodsMoney;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty(value = "实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "收货人")
    private String addressContact;

    @ApiModelProperty(value = "收货人电话")
    private String addressMobile;

    @ApiModelProperty(value = "收货人地址")
    private String addressContent;

    @ApiModelProperty(value = "是否支付: [0=未支付, 1=已支付]")
    private Integer payIs;

    @ApiModelProperty(value = "订单状态: [0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消]")
    private Integer orderStatus;

    @ApiModelProperty(value = "是否发货: [0=未发货, 1=已发货]")
    private Integer expressIs;

    @ApiModelProperty(value = "支付方式: [0=未知, 1=余额, 2=微信, 3=支付宝]")
    private Integer payWay;

    @ApiModelProperty(value = "取消时间")
    private Integer cancelTime;
    @ApiModelProperty("核销状态:0-待核销;1-已核销;")
    private Integer verificationStatus;
    @ApiModelProperty("核销时间")
    private Long verificationTime;
    @ApiModelProperty("自提门店ID")
    private Integer selffetchShopId;
    @ApiModelProperty("配送方式")
    private Integer deliveryType;
    @ApiModelProperty("订单类型")
    private Integer orderType;

}
