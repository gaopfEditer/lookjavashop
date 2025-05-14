package com.mdd.front.vo.selffetchOrder;

import com.mdd.front.vo.order.OrderGoodsListVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("自提订单详情Vo")
public class SelffetchOrderDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "订单类型")
    private Integer orderType;

    @ApiModelProperty(value = "订单类型")
    private String orderTypeMsg;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "收货人")
    private String addressContact;

    @ApiModelProperty(value = "收货电话")
    private Long addressMobile;

    @ApiModelProperty(value = "收货地址")
    private String addressContent;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @ApiModelProperty(value = "商品金额")
    private BigDecimal goodsMoney;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal expressMoney;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponMoney;

    @ApiModelProperty(value = "应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty(value = "配送方式: [0=快递配送]")
    private Integer deliveryType;
    @ApiModelProperty(value = "配送方式字符串")
    private String deliveryTypeStr;
    @ApiModelProperty(value = "是否支付: [0=未支付, 1=已支付]")
    private Integer payIs;

    @ApiModelProperty(value = "支付方式: [0=未知, 1=余额, 2=微信, 3=支付宝]")
    private Integer payWay;

    @ApiModelProperty(value = "下单时间")
    private String createTime;

    @ApiModelProperty(value = "付款时间")
    private String payTime;

    @ApiModelProperty(value = "发货时间")
    private String expressTime;

    @ApiModelProperty(value = "成交时间")
    private String confirmTime;

    @ApiModelProperty(value = "支付剩余时间")
    private Long cancelTime;

    @ApiModelProperty(value = "订单状态: [0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消]")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单状态描述")
    private String orderStatusMsg;

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

    @ApiModelProperty(value = "评价按钮")
    private Integer commentBtn;

    @ApiModelProperty(value = "物流按钮")
    private Integer logisticsBtn;

    @ApiModelProperty(value = "售后按钮")
    private Integer refundBtn;
    @ApiModelProperty(value = "核销码")
    private String pickupCode;

    @ApiModelProperty(value = "自提点信息")
    private SelffetchShopDetailVo selffetchShop;

    @ApiModelProperty(value = "核销状态")
    private Integer verificationStatus;
    @ApiModelProperty(value = "核销状态")
    private String verificationStatusStr;
    @ApiModelProperty(value = "核销时间")
    private String verificationTimeStr;
}
