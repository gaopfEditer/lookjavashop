package com.mdd.front.validate.cart;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("购物车变动参数")
public class CartChangeNumValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id参数缺失")
    @IDMust(message = "id参数必须大于0")
    @ApiModelProperty(value = "购物车ID", required = true)
    private Integer id;

    @NotNull(message = "数量参数缺失")
    @IDMust(message = "数量必须大于0")
    @ApiModelProperty(value = "变动数量", required = true)
    private Integer num;
}
