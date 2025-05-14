package com.mdd.admin.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("商品规格Vo")
public class GoodsSkuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "ids")
    private String ids;

    @ApiModelProperty(value = "商品ID")
    private Integer goodsId;

    @ApiModelProperty(value = "规格图片")
    private String image;

    @ApiModelProperty(value = "sku值(ID集合)")
    private String skuValueIds;

    @ApiModelProperty(value = "sku值(值集合)")
    private String skuValueArr;

    @ApiModelProperty(value = "销售价")
    private BigDecimal price;

    @ApiModelProperty(value = "划线价")
    private BigDecimal linePrice = new BigDecimal(0);

    @ApiModelProperty(value = "市场价")
    private BigDecimal marketPrice = new BigDecimal(0);

    @ApiModelProperty(value = "库存")
    private Integer stock = 0;

    @ApiModelProperty(value = "重量")
    private Double weight = 0.0;

    @ApiModelProperty(value = "体积")
    private Double volume = 0.0;

    @ApiModelProperty(value = "条形码")
    private String code;

}
