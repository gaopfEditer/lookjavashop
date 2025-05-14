package com.mdd.admin.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
@ApiModel("核销搜素参数")
public class VerificationSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    private Integer orderId;

    @ApiModelProperty(value = "自提门店ID")
    private Integer selffetchShopId;

    @ApiModelProperty(value = "操作人ID(0为系统核销)")
    private Integer handleId;

    @ApiModelProperty(value = "核销场景:0-系统;1-管理员;2-会员;")
    private Integer verificationScene;

    @ApiModelProperty(value = "核销员数据快照")
    private String snapshot;

}
