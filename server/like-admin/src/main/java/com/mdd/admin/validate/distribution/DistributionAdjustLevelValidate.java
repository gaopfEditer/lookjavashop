package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@ApiModel("调整分销等级参数")
public class DistributionAdjustLevelValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "用户参数缺失")
    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @NotNull(message = "等级ID参数缺失")
    @ApiModelProperty(value = "等级ID", required = true)
    private Integer levelId;

}
