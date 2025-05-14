package com.mdd.front.vo.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "订单结算优惠券Vo")
public class CouponOrderResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "可用优惠券数量")
    private Integer ableUseCount;

    @ApiModelProperty(value = "不可用优惠券数量")
    private Integer unableUseCount;

    @ApiModelProperty(value = "可用优惠券")
    private List<CouponOrderSettleVo> ableUse;

    @ApiModelProperty(value = "不可用优惠券")
    private List<CouponOrderSettleVo> unableUse;

}
