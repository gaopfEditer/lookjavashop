package com.mdd.front.validate.cart;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("购物车变动规格参数")
public class CartAddValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品规格ID参数缺失")
    @IDMust(message = "商品规格ID参数必须大于0")
    @ApiModelProperty(value = "商品规格ID", required = true)
    private Integer goodsSkuId;

    @NotNull(message = "商品数量参数缺失")
    @ApiModelProperty(value = "商品数量", required = true)
    private Integer num;
}
