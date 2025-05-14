package com.mdd.front.vo.order;

import com.mdd.common.entity.selffetchshop.SelffetchShop;
import com.mdd.front.vo.selffetchOrder.SelffetchShopLastDetailVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.front.vo.user.UserAddressVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "订单结算Vo")
public class OrderSettleResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户地址")
    private UserAddressVo address;

    @ApiModelProperty(value = "订单商品")
    private List<OrderGoodsInfoVo> goods;

    @ApiModelProperty(value = "订单总价")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "订单商品数量")
    private Integer totalNum;

    @ApiModelProperty(value = "商品总价")
    private BigDecimal goodsAmount;

    @ApiModelProperty(value = "订单应付金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单邮费")
    private BigDecimal freightAmount;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "订单类型 [1=普通订单 , 2=秒杀订单]")
    private Integer orderType;

    @ApiModelProperty(value = "配送方式")
    private Integer deliveryType;

    @ApiModelProperty(value = "秒杀活动ID")
    private Integer seckillId;

    @ApiModelProperty(value = "优惠券领取记录ID")
    private Integer couponListId;

    @ApiModelProperty(value = "配送方式描述")
    private String deliveryTypeDesc;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "自提门店ID")
    private Integer selffetchShopId;

    @ApiModelProperty(value = "上次联系人")
    private String lastAddressContact;

    @ApiModelProperty(value = "上次联系电话")
    private String lastAddressMobile;

    @ApiModelProperty(value = "上次自提点")
    private SelffetchShopLastDetailVo lastSelffetchShop;

}
