package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("提现设置Vo")
public class SettingWithdrawVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提现方式: 1-钱包余额；2-微信零钱；3-银行卡；4-微信收款码；5-支付宝收款码")
    private List<Integer> withdrawWay;

    @ApiModelProperty(value = "微信零钱接口: 1-商家转账到零钱")
    private Integer transferWay;

    @ApiModelProperty(value = "最低提现金额")
    private BigDecimal minMoney;

    @ApiModelProperty(value = "最高提现金额")
    private BigDecimal maxMoney;

    @ApiModelProperty(value = "提现手续费")
    private Double serviceCharge;
}
