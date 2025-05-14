package com.mdd.admin.validate.finance;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("提现转账参数")
public class FinanceWithdrawTransferValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提现记录id")
    private Integer id;

    @IntegerContains(values = {0, 1}, message = "转账操作参数不在合法值内")
    @ApiModelProperty(value = "转账操作 1=转账成功 0=转账拒绝")
    private Integer status;

    @ApiModelProperty(value = "转账凭证")
    private String transferVoucher;

    @ApiModelProperty(value = "转账备注")
    private String transferRemark;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

}
