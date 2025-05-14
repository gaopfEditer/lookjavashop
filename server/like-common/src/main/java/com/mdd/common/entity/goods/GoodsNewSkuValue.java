package com.mdd.common.entity.goods;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品SKU值实体")
public class GoodsNewSkuValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("商品id")
    private Integer goodsId;

    @ApiModelProperty("sku名称id")
    private Integer skuNameId;

    @ApiModelProperty("sku值")
    private String  value;

}
