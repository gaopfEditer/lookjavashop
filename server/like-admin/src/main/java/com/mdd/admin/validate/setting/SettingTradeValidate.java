package com.mdd.admin.validate.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("交易设置参数")
public class SettingTradeValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "系统自动取消订单必填")
    @ApiModelProperty(value = "系统自动取消订单: -1=关闭,否则开启(天)")
    private Float cancelUnpaidOrderTime;

    @NotNull(message = "订单允许取消时长必填")
    @ApiModelProperty(value = "订单允许取消时长: -1=关闭,否则开启(分钟)")
    private Float cancelUnshippedOrderTime;

    @NotNull(message = "订单自动完成时长必填")
    @ApiModelProperty(value = "订单自动完成时长: -1=关闭,否则开启(天)")
    private Float autoConfirmReceiptDay;

    @NotNull(message = "买家售后维权时效必填")
    @ApiModelProperty(value = "买家售后维权时效: -1=关闭,否则开启(天)")
    private Float afterSalesDay;

    @NotNull(message = "库存占用时机必选")
    @ApiModelProperty(value = "库存占用时机: 1=订单提交占用")
    private Integer inventoryOccupancy;

    @NotNull(message = "取消订单退回库存必选")
    @ApiModelProperty(value = "取消订单退回库存: 0=否,1=是")
    private Integer returnInventory;

    @NotNull(message = "取消订单退回优惠券必选")
    @ApiModelProperty(value = "取消订单退回优惠券: 0=否,1=是")
    private Integer returnCoupon;

    @NotNull(message = "未发货申请退款必须严")
    @ApiModelProperty(value = "未发货申请退款: 0=否,1=是")
    private Integer beforeExpressRefund;


}
