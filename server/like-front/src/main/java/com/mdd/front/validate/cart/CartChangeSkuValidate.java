package com.mdd.front.validate.cart;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("购物车变动规格参数")
public class CartChangeSkuValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "购物车ID参数缺失")
    @IDMust(message = "购物车ID参数必须大于0")
    @ApiModelProperty(value = "购物车ID", required = true)
    private Integer id;

    @NotNull(message = "商品规格ID参数缺失")
    @IDMust(message = "商品规格ID参数必须大于0")
    @ApiModelProperty(value = "商品规格ID", required = true)
    private Integer goodsSkuId;

    @NotNull(message = "数量参数缺失")
    @IDMust(message = "数量必须大于0")
    @ApiModelProperty(value = "变动数量", required = true)
    private Integer num;
}
