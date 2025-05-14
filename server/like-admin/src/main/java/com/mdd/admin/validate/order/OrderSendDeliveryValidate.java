package com.mdd.admin.validate.order;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("订单发货参数")
public class OrderSendDeliveryValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "orderId参数必传且需大于0")
    @ApiModelProperty(value = "订单ID", required = true)
    private Integer orderId;

    @IntegerContains(values = {1, 2})
    @ApiModelProperty(value = "配送方式: [1=快递配送, 2=无需快递]", required = true)
    private Integer sendType;

    @ApiModelProperty(value = "物流公司")
    private Integer expressId;

    @ApiModelProperty(value = "物流单号")
    private String invoiceNo;

    @ApiModelProperty(value = "发货备注")
    private String remark;

}
