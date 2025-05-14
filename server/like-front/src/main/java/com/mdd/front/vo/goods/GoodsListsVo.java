package com.mdd.front.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "商品列表Vo")
public class GoodsListsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "商品名称")
    private String Name;

    @ApiModelProperty(value = "商品主图")
    private String image;

    @ApiModelProperty(value = "销售数量")
    private Integer salesNum;

    @ApiModelProperty(value = "销售价格")
    private BigDecimal price;

    @ApiModelProperty(value = "商品状态:[1=上架, 0=下架]")
    private Integer status;

    @ApiModelProperty(value = "划线价格")
    private BigDecimal lineationPrice;
}
