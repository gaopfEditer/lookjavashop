package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("调整上级参数")
public class DistributionAdjustLeaderValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "调整类型参数缺失")
    @ApiModelProperty(value = "调整类型: [assign=指定推荐人 system=系统]", required = true)
    private String type;

    @NotNull(message = "用户参数缺失")
    @ApiModelProperty(value = "用户ID", required = true)
    private Integer userId;

    @ApiModelProperty(value = "上级ID")
    private Integer firstId;


}
