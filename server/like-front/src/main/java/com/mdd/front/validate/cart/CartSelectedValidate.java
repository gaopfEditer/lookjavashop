package com.mdd.front.validate.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("购物车选中参数")
public class CartSelectedValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids参数缺失")
    @ApiModelProperty(value = "购物车ID", required = true)
    private List<Integer> ids;

    @NotNull(message = "选中参数缺失")
    @ApiModelProperty(value = "选中状态 0=未选中 1=选中", required = true)
    private Integer isSelected;

}
