package com.mdd.front.validate.coupon;

import com.mdd.front.validate.order.BuyGoodsValidate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 订单结算优惠券参数
 */
@Data
@ApiModel(value = "订单优惠券参数")
public class CouponOrderSettleValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "购买类型参数缺失")
    @ApiModelProperty("购买类型 [buyNow=立即购买, cart=购物车]")
    private String buyType;

    @NotNull(message = "订单商品参数缺失")
    @ApiModelProperty(value = "订单商品信息")
    private List<BuyGoodsValidate> buyGoods;



}
