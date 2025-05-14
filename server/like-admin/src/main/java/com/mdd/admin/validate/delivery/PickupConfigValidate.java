package com.mdd.admin.validate.delivery;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("到店自提配置参数")
public class PickupConfigValidate {
    @NotEmpty(message = "到店自提名称不能为空")
    @ApiModelProperty(value = "到店自提名称")
    private String pickupName;

    @NotEmpty(message = "到店自提别名不能为空")
    @ApiModelProperty(value = "到店自提别名")
    private String pickupAlias;

    @NotNull(message = "缺少状态参数")
    @IntegerContains(values = {0, 1}, message = "expressIs不在合法值内")
    @ApiModelProperty(value = "是否开启")
    private Integer pickupIs;

}
