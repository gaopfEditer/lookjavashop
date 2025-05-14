package com.mdd.front.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "售后买家退款参数")
public class OrderAfterDeliveryValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "售后ID参数缺失")
    @ApiModelProperty(value = "售后ID", required = true)
    private Integer afterId;

    @NotNull(message = "请选择物流公司")
    @ApiModelProperty(value = "物流公司ID", required = true)
    private Integer expressId;

    @NotNull(message = "请填写物流单号")
    @ApiModelProperty(value = "物流单号", required = true)
    private String invoiceNo;

    @ApiModelProperty(value = "备注说明")
    private String expressRemark;

    @NotNull(message = "请填写联系方式")
    @ApiModelProperty(value = "联系方式", required = true)
    private String expressContact;

}

