package com.mdd.front.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "申请售后参数")
public class OrderAfterCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "订单商品ID参数丢失")
    @ApiModelProperty(value = "订单商品ID")
    private Integer orderGoodsId;

    @NotNull(message = "请选择退款方式")
    @ApiModelProperty(value = "退款方式: [1=仅退款 2=退货退款]")
    private Integer refundType;

    @NotNull(message = "请填写退款金额")
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundMoney;

    @NotNull(message = "请选择退款原因")
    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    @ApiModelProperty(value = "退款说明")
    private String refundRemark;

    @NotNull(message = "请上传凭证")
    @ApiModelProperty(value = "凭证")
    private List<String> refundImage;
}

