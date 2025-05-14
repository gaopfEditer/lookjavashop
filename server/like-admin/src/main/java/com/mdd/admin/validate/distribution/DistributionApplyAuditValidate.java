package com.mdd.admin.validate.distribution;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("分销申请审核参数")
public class DistributionApplyAuditValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "审核记录ID", required = true)
    private Integer id;

    @NotNull
    @ApiModelProperty(value = "审核状态: [1=审核通过 0=审核不通过]")
    private Integer status;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;
}
