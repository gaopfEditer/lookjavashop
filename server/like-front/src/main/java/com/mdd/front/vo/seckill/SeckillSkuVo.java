package com.mdd.front.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("秒杀SkuVo")
public class SeckillSkuVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规格ID")
    private Integer skuId;

    @ApiModelProperty(value = "规格名称")
    private String skuValueStr;

    @ApiModelProperty(value = "销售价格")
    private BigDecimal sellPrice;

    @ApiModelProperty(value = "秒杀价格")
    private BigDecimal seckillPrice;

}
