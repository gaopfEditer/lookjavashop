package com.mdd.front.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("分销绑定参数")
public class DistributionBindValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "邀请码缺失")
    @ApiModelProperty(value = "邀请码", required = true)
    private String code;
}
