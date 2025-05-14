package com.mdd.front.vo.withdraw;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "提现配置vo")
public class WithdrawConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "可提现佣金")
    private BigDecimal ableWithdraw;

    @ApiModelProperty(value = "手续费百分比")
    private Double serviceCharge;

    @ApiModelProperty(value = "最低提现金额")
    private BigDecimal minMoney;

    @ApiModelProperty(value = "最高提现金额")
    private BigDecimal maxMoney;

    @ApiModelProperty(value = "提现方式")
    private List<type> type;

    @Data
    public static class type {
        @ApiModelProperty(value = "提现方式名称")
        private String name;

        @ApiModelProperty(value = "提现方式值")
        private Integer value;
    }

}
