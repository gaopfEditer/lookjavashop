package com.mdd.admin.validate.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("发放优惠券参数")
public class SendCouponValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private List<Integer> userIds;

    @ApiModelProperty(value = "优惠券ID")
    private List<Integer> couponIds;

    @ApiModelProperty(value = "发放数量")
    private Integer number;

}
