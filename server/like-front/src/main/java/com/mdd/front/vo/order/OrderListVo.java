package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("订单列表Vo")
public class OrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty(value = "是否支付: [0=未支付, 1=已支付]")
    private Integer payIs;

    @ApiModelProperty(value = "支付方式: [0=未知, 1=余额, 2=微信]")
    private Integer payWay;

    @ApiModelProperty(value = "是否发货: [0=未发货, 1=已发货]")
    private Integer expressIs;

    @ApiModelProperty(value = "订单状态: [0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消]")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单状态描述")
    private String orderStatusMsg;

    @ApiModelProperty(value = "支付剩余时间")
    private Long cancelTime;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "订单商品")
    private List<OrderGoodsListVo> orderGoodsList;

    @ApiModelProperty(value = "支付按钮")
    private Integer payBtn;

    @ApiModelProperty(value = "取消按钮")
    private Integer cancelBtn;

    @ApiModelProperty(value = "确认按钮")
    private Integer confirmBtn;

    @ApiModelProperty(value = "删除按钮")
    private Integer deleteBtn;

    @ApiModelProperty(value = "物流按钮")
    private Integer logisticsBtn;

    @ApiModelProperty(value = "申请退款按钮")
    private Integer refundBtn;

    @ApiModelProperty(value = "评价按钮")
    private Integer commentBtn;
    @ApiModelProperty(value = "配送方式")
    private Integer deliveryType;

    @ApiModelProperty(value = "核销状态")
    private Integer verificationStatus;
    @ApiModelProperty(value = "核销状态")
    private String verificationStatusStr;

}
