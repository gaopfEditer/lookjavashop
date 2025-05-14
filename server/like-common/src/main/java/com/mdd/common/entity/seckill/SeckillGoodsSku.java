package com.mdd.common.entity.seckill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("秒杀规格实体")
public class SeckillGoodsSku {

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("秒杀活动ID")
    private Integer seckillId;

    @ApiModelProperty("秒杀商品表ID")
    private Integer seckillGid;

    @ApiModelProperty("商品规格ID")
    private Integer skuId;

    @ApiModelProperty("规格名称")
    private String skuValueStr;

    @ApiModelProperty("原始售价")
    private BigDecimal sellPrice;

    @ApiModelProperty("秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty("销售金额")
    private BigDecimal salesAmount;

    @ApiModelProperty("销售数量")
    private Integer salesVolume;

    @ApiModelProperty("成交单数")
    private Integer closingOrder;

}
