package com.mdd.admin.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "提现详情Vo")
public class FinanceWithdrawDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提现记录ID")
    private Integer id;

    @ApiModelProperty(value = "提现编号")
    private String sn;

    @ApiModelProperty(value = "用户编号")
    private Integer userSn;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "提现类型")
    private Integer type;

    @ApiModelProperty(value = "提现类型")
    private String typeMsg;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal money;

    @ApiModelProperty(value = "手续费")
    private BigDecimal handlingFee;

    @ApiModelProperty(value = "实际到账金额")
    private BigDecimal leftMoney;

    @ApiModelProperty(value = "收款码")
    private String moneyQrCode;

    @ApiModelProperty(value = "申请备注")
    private String applyRemark;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    @ApiModelProperty(value = "转账凭证")
    private String transferVoucher;

    @ApiModelProperty(value = "转账备注")
    private String transferRemark;

    @ApiModelProperty(value = "银行")
    private String bank;

    @ApiModelProperty(value = "银行支行")
    private String subBank;

    @ApiModelProperty(value = "状态 1-待提现;2-提现中;3-提现成功;4-提现失败")
    private Integer status;

    @ApiModelProperty(value = "状态")
    private String statusMsg;

    @ApiModelProperty(value = "提现时间")
    private String createTime;

}
