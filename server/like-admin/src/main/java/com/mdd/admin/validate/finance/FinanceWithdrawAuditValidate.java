package com.mdd.admin.validate.finance;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("提现审核参数")
public class FinanceWithdrawAuditValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提现记录id")
    private Integer id;

    @IntegerContains(values = {0, 1}, message = "审核状态参数不在合法值内")
    @ApiModelProperty(value = "审核状态 1=审核成功 0=审核拒绝")
    private Integer status;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

}
