package com.mdd.common.entity.goods;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品SKU实体")
public class GoodsNewSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("规格图片")
    private String image;

    @ApiModelProperty("sku值(ID集合)")
    private String skuValueIds;

    @ApiModelProperty("sku值(值集合)")
    private String skuValueArr;

    @ApiModelProperty("销售价")
    private BigDecimal price;

    @ApiModelProperty("划线价")
    private BigDecimal linePrice;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("重量")
    private Double weight;

    @ApiModelProperty("体积")
    private Double volume;

    @ApiModelProperty("条形码")
    private String code;
    @Version
    private Integer version;

}
