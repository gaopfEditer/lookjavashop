package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "售后列表Vo")
public class OrderAfterListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "售后ID")
    private Integer afterId;

    @ApiModelProperty(value = "订单商品id")
    private Integer orderGoodsId;

    @ApiModelProperty(value = "商品主图")
    private String goodsImage;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品规格值")
    private String goodsSkuValue;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "售后申请时间")
    private String afterCreateTime;

    @ApiModelProperty(value = "下单时间")
    private Long orderCreateTime;

    @ApiModelProperty(value = "确认收货时间")
    private Long orderConfirmTime;

    @ApiModelProperty(value = "退款类型: [1=仅退款 2=退货退款]")
    private Integer refundType;

    @ApiModelProperty(value = "退款类型描述")
    private String refundTypeMsg;

    @ApiModelProperty(value = "售后状态")
    private Integer afterStatus;

    @ApiModelProperty(value = "售后状态")
    private Integer subStatus;

    @ApiModelProperty("售后状态描述")
    private String afterStatusMsg;

    @ApiModelProperty("售后状态描述")
    private String subStatusMsg;

    @ApiModelProperty(value = "申请售后按钮: [0=不显示 1=显示]")
    private Integer applyBtn;

    @ApiModelProperty(value = "撤销申请按钮: [0=不显示 1=显示]")
    private Integer cancelBtn;

    @ApiModelProperty(value = "重新申请按钮: [0=不显示 1=显示]")
    private Integer reapplyBtn;

    @ApiModelProperty(value = "填写单号按钮: [0=不显示 1=显示]")
    private Integer deliveryBtn;

}
