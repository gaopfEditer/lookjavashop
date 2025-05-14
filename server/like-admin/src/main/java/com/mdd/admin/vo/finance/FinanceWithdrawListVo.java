package com.mdd.admin.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("提现记录列表Vo")
public class FinanceWithdrawListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    private Integer id;

    @ApiModelProperty(value = "提现单号")
    private String sn;

    @ApiModelProperty(value = "用户编号")
    private String userSn;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal money;

    @ApiModelProperty(value = "手续费")
    private BigDecimal handlingFee;

    @ApiModelProperty(value = "到账金额")
    private BigDecimal leftMoney;

    @ApiModelProperty(value = "提现方式")
    private Integer type;

    @ApiModelProperty(value = "提现方式")
    private String typeMsg;

    @ApiModelProperty(value = "提现状态")
    private Integer status;

    @ApiModelProperty(value = "提现状态")
    private String statusMsg;

    @ApiModelProperty(value = "提现备注")
    private String applyRemark;

    @ApiModelProperty(value = "记录时间")
    private String createTime;

}
