package com.mdd.front.validate.withdraw;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("提现申请参数")
public class WithdrawApplyValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "请选择提现类型")
    @IntegerContains(values = {1, 2, 3, 4, 5}, message = "提现类型值异常")
    @ApiModelProperty(value = "提现类型: 1-钱包余额；2-微信零钱；3-银行卡；4-微信收款码；5-支付宝收款码", required = true)
    private Integer type;

    @NotNull(message = "请输入提现金额")
    @Min(value = 0, message = "提现金额不能少于0")
    @ApiModelProperty(value = "提现金额", required = true)
    private BigDecimal money;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "收款二维码")
    private String moneyQrCode;

    @ApiModelProperty(value = "提现银行")
    private String bank;

    @ApiModelProperty(value = "提现支行")
    private String subBank;

    @ApiModelProperty(value = "申请备注")
    private String applyRemark;


}
