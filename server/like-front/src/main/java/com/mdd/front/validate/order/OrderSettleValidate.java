package com.mdd.front.validate.order;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.front.vo.user.UserAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 订单结算参数
 */
@Data
@ApiModel(value = "订单结算参数")
public class OrderSettleValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "购买类型参数缺失")
    @ApiModelProperty("购买类型 [buyNow=立即购买, cart=购物车]")
    private String buyType;

    @ApiModelProperty(value = "地址ID")
    private Integer addressId;

    @ApiModelProperty(value = "优惠券领取记录ID")
    private Integer couponListId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @NotNull(message = "订单商品参数缺失")
    @ApiModelProperty(value = "订单商品信息")
    private List<BuyGoodsValidate> buyGoods;

    @ApiModelProperty(value = "到店自提")
    private UserAddressVo address;
    @ApiModelProperty(value = "配送类型")
    private Integer deliveryType;
    @ApiModelProperty(value = "自提门店ID")
    private Integer selffetchShopId;


}
