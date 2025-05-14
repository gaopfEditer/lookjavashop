package com.mdd.front.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("秒杀详情Vo")
public class SeckillDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    private Integer goodsId;

    @ApiModelProperty(value = "秒杀活动ID")
    private Integer seckillId;

    @ApiModelProperty(value = "秒杀商品关联id")
    private Integer seckillGid;

    @ApiModelProperty(value = "秒杀名称")
    private String seckillName;

    @ApiModelProperty(value = "秒杀说明")
    private String seckillRemarks;

    @ApiModelProperty(value = "最低秒杀价")
    private BigDecimal minSeckillPrice;

    @ApiModelProperty(value = "销售量")
    private Integer salesVolume;

    @ApiModelProperty(value = "浏览量")
    private Integer browseVolume;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "商品规格")
    private List<SeckillSkuVo> goodsSku;

}
