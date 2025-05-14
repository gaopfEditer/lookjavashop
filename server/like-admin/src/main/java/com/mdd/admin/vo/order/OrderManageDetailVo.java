package com.mdd.admin.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("订单详情Vo")
public class OrderManageDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;
    @ApiModelProperty(value = "订单编号")
    private String orderSn;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "下单用户")
    private String nickname;

    @ApiModelProperty(value = "总的金额")
    private BigDecimal money;
    @ApiModelProperty(value = "商品金额")
    private BigDecimal goodsMoney;
    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal couponMoney;
    @ApiModelProperty(value = "运费金额")
    private BigDecimal expressMoney;
    @ApiModelProperty(value = "实付金额")
    private BigDecimal payMoney;
    @ApiModelProperty(value = "应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty(value = "是否支付: [0=未支付, 1=已支付]")
    private Integer payIs;
    @ApiModelProperty(value = "支付方式: [0=未知, 1=余额, 2=微信, 3=支付宝]")
    private Integer payWay;
    @ApiModelProperty(value = "支付方式描述")
    private String payWayMsg;
    @ApiModelProperty(value = "是否支付描述")
    private String payIsMsg;
    @ApiModelProperty(value = "订单状态: [0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消]")
    private Integer orderStatus;
    @ApiModelProperty(value = "订单状态描述")
    private String orderStatusMsg;
    @ApiModelProperty(value = "订单来源描述")
    private String orderSourceMsg;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;
    @ApiModelProperty(value = "商家备注")
    private String shopRemark;

    @ApiModelProperty(value = "收货人")
    private String addressContact;
    @ApiModelProperty(value = "收货电话")
    private String addressMobile;
    @ApiModelProperty(value = "收货地址")
    private String addressContent;

    @ApiModelProperty(value = "配送方式: [1=快递]")
    private Integer deliveryType;
    @ApiModelProperty(value = "配送方式描述")
    private String deliveryTypeMsg;

    @ApiModelProperty(value = "是否发货: [0=未发货, 1=已发货]")
    private Integer expressIs;
    @ApiModelProperty(value = "发货状态描述")
    private String expressIsMsg;
    @ApiModelProperty(value = "物流单号")
    private String expressNo;
    @ApiModelProperty(value = "物流公司")
    private String expressCompanyName;
    @ApiModelProperty(value = "发货时间")
    private String expressConfirmTime;

    @ApiModelProperty(value = "取消按钮")
    private Integer cancelBtn;
    @ApiModelProperty(value = "确认按钮")
    private Integer confirmBtn;
    @ApiModelProperty(value = "删除按钮")
    private Integer deleteBtn;
    @ApiModelProperty(value = "发货按钮")
    private Integer deliverBtn;
    @ApiModelProperty(value = "物流按钮")
    private Integer logisticsBtn;
    @ApiModelProperty(value = "售后按钮")
    private Integer refundBtn;

    @ApiModelProperty("支付时间")
    private String payTime;
    @ApiModelProperty("取消时间")
    private String cancelTime;
    @ApiModelProperty("确认时间")
    private String confirmTime;
    @ApiModelProperty("发货时间")
    private String expressTime;
    @ApiModelProperty("下单时间")
    private String createTime;

    @ApiModelProperty("核销状态:0-待核销;1-已核销;")
    private Integer verificationStatus;
    @ApiModelProperty("核销时间")
    private Long verificationTime;
    @ApiModelProperty("核销时间字符串")
    private String verificationTimeStr;
    @ApiModelProperty("自提门店ID")
    private Integer selffetchShopId;

    @ApiModelProperty("自提门店名称")
    private String selffetchShopName;
    @ApiModelProperty("自提门店地址")
    private String selffetchShopAddress;
    @ApiModelProperty("自提门店电话")
    private String selffetchShopMobile;
    @ApiModelProperty("核销码")
    private String pickupCode;
    @ApiModelProperty("核销员")
    private String verificationByStr;
    @ApiModelProperty(value = "订单商品")
    private List<OrderGoodsListVo> orderGoodsList;

    @ApiModelProperty(value = "订单日志")
    private List<OrderLogListVo> orderLogList;

}
