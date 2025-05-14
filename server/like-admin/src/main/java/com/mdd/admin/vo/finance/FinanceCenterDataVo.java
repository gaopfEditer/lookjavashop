package com.mdd.admin.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("财务中心数据Vo")
public class FinanceCenterDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "累计营业额(元)")
    private BigDecimal totalOrderAmount;

    @ApiModelProperty(value = "累计成交订单(笔)")
    private Long totalOrderNum;

    @ApiModelProperty(value = "已退款订单金额(元)")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty(value = "待退款订单金额(元)")
    private BigDecimal waitRefundAmount;

    @ApiModelProperty(value = "用户充值金额(元)")
    private BigDecimal userRechargeAmount;

    @ApiModelProperty(value = "用户可用余额(元)")
    private BigDecimal userUsableMoney;

    @ApiModelProperty(value = "用户已用余额(元)")
    private BigDecimal userUsedMoney;

    @ApiModelProperty(value = "累计充值人数/人次")
    private Integer totalRechargeNum;

}
