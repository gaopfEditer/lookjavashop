package com.mdd.admin.vo.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("营销优惠券列表Vo")
public class MarketingCouponSelectVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券编号")
    private String sn;

    @ApiModelProperty(value = "优惠券内容")
    private String discountContent;

    @ApiModelProperty(value = "发放方式")
    private String sendTypeMsg;

}
