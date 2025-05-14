package com.mdd.front.vo.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "商品优惠券Vo")
public class CouponGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal money;

    @ApiModelProperty(value = "使用场景")
    private String useScene;

    @ApiModelProperty(value = "使用条件")
    private String condition;

    @ApiModelProperty(value = "有效时间")
    private String effectiveTime;

    @ApiModelProperty(value = "是否已领取")
    private Integer isReceive;

    @ApiModelProperty(value = "是否可领取")
    private Integer isAble;

}
