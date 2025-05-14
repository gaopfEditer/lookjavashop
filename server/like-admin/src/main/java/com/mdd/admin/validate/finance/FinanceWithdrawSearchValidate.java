package com.mdd.admin.validate.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("提现记录搜索参数")
public class FinanceWithdrawSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "类型 1-钱包余额；2-微信零钱；3-银行卡;4-微信收款码;5-支付宝收款码")
    private Integer type;

    @ApiModelProperty(value = "状态 1-待提现;2-提现中;3-提现成功;4-提现失败")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Integer startTime;

    @ApiModelProperty(value = "结束时间")
    private Integer endTime;

}
