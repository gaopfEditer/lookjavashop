package com.mdd.admin.validate.delivery;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("物流配置参数")
public class DeliverConfigValidate {

    @NotEmpty(message = "快递名称不能为空")
    @ApiModelProperty(value = "快递名称")
    private String expressName;

    @NotEmpty(message = "快递别名不能为空")
    @ApiModelProperty(value = "快递别名")
    private String expressAlias;

    @NotNull(message = "缺少状态参数")
    @IntegerContains(values = {0, 1}, message = "expressIs不在合法值内")
    @ApiModelProperty(value = "是否开启")
    private Integer expressIs;

}
