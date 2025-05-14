package com.mdd.admin.validate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.*;

@Data
@ApiModel("核销创建参数")
public class VerificationCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "orderId参数缺失")
    @ApiModelProperty(value = "订单ID")
    private Integer orderId;

    @NotNull(message = "selffetchShopId参数缺失")
    @ApiModelProperty(value = "自提门店ID")
    private Integer selffetchShopId;

    @NotNull(message = "handleId参数缺失")
    @ApiModelProperty(value = "操作人ID(0为系统核销)")
    private Integer handleId;

    @NotNull(message = "verificationScene参数缺失")
    @ApiModelProperty(value = "核销场景:0-系统;1-管理员;2-会员;")
    private Integer verificationScene;

    @NotNull(message = "snapshot参数缺失")
    @ApiModelProperty(value = "核销员数据快照")
    private String snapshot;

}
