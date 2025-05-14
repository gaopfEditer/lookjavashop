package com.mdd.front.vo.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "购物车商品Vo")
public class CartGoodsListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "购物车ID")
    private Integer id;

    @ApiModelProperty(value = "商品ID")
    private Integer goodsId;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品图")
    private String image;

    @ApiModelProperty(value = "规格ID")
    private String goodsSkuId;

    @ApiModelProperty(value = "规格类型：[1=单规格, 2=多规格]")
    private Integer specType;

    @ApiModelProperty(value = "规格值")
    private String skuValueArr;

    @ApiModelProperty(value = "数量")
    private Integer num;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "选中状态")
    private Integer selected;

    @ApiModelProperty(value = "商品删除状态")
    private Integer goodsDelete;

    @ApiModelProperty(value = "商品状态")
    private Integer goodsStatus;

    @ApiModelProperty(value = "购物车状态: [0=正常, 1=商品下架, 2=商品删除, 3=规格删除, 4=sku库存不足]")
    private Integer cartStatus;

}
