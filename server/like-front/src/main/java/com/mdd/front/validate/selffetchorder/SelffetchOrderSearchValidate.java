package com.mdd.front.validate.selffetchorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "到点自提列表参数")
public class SelffetchOrderSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单商品ID")
    private Integer orderStatus;

    @ApiModelProperty(value = "自提门店ID")
    private Integer selffetchShopId;

    @ApiModelProperty(value = "核销状态:0-待核销;1-已核销;")
    private Integer verificationStatus;

    @ApiModelProperty(value = "配送方式")
    private Integer deliveryType;

    @ApiModelProperty(value = "当前用户id")
    private Integer currUserId;
}

