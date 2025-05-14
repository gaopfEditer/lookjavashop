package com.mdd.front.vo.selffetchOrder;

import com.mdd.front.vo.order.OrderGoodsListVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("自提核销号结果Vo")
public class SelffetchOrderPickuplVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "核销状态")
    private Integer verificationStatus;
    @ApiModelProperty(value = "核销状态")
    private String verificationStatusStr;
    @ApiModelProperty(value = "核销时间")
    private String verificationTimeStr;
}
