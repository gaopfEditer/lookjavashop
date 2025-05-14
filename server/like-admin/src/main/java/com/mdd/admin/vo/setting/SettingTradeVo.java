package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("交易设置Vo")
public class SettingTradeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "系统自动取消订单: -1=关闭,否则开启(天)")
    private Float cancelUnpaidOrderTime;

    @ApiModelProperty(value = "订单允许取消时长: -1=关闭,否则开启(分钟)")
    private Float cancelUnshippedOrderTime;

    @ApiModelProperty(value = "订单自动完成时长: -1=关闭,否则开启(天)")
    private Float autoConfirmReceiptDay;

    @ApiModelProperty(value = "买家售后维权时效: -1=关闭,否则开启(天)")
    private Float afterSalesDay;

    @ApiModelProperty(value = "库存占用时机: 1=订单提交占用")
    private Integer inventoryOccupancy;

    @ApiModelProperty(value = "取消订单退回库存: 0=否,1=是")
    private Integer returnInventory;

    @ApiModelProperty(value = "取消订单退回优惠券: 0=否,1=是")
    private Integer returnCoupon;

    @ApiModelProperty(value = "未发货申请退款: 0=否,1=是")
    private Integer beforeExpressRefund;


}
