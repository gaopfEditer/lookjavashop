package com.mdd.front.vo.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "领券中心Vo")
public class CouponListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer Id;

    @ApiModelProperty(value = "优惠券编码")
    private String sn;

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

    @ApiModelProperty(value = "能否领取: [0=不能 1=可以]")
    private Integer isAble;

    @ApiModelProperty(value = "优惠券库存: [0=没有库存 1=有库存]")
    private Integer isEmpty;

    @ApiModelProperty(value = "是否已领取 0=未领取 1=已领取")
    private Integer isReceive;

}
