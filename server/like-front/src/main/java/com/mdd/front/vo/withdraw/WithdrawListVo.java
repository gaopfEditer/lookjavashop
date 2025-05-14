package com.mdd.front.vo.withdraw;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "提现列表Vo")
public class WithdrawListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "提现记录ID")
    private Integer id;

    @ApiModelProperty(value = "提现类型")
    private Integer type;

    @ApiModelProperty(value = "提现类型")
    private String typeMsg;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal money;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    @ApiModelProperty(value = "状态 1-待提现;2-提现中;3-提现成功;4-提现失败")
    private Integer status;

    @ApiModelProperty(value = "状态")
    private String statusMsg;

    @ApiModelProperty(value = "提现时间")
    private String createTime;

}
