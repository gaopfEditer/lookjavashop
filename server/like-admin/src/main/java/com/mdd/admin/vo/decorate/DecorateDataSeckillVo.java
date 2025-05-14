package com.mdd.admin.vo.decorate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("装修秒杀数据Vo")
public class DecorateDataSeckillVo implements Serializable {

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("秒杀名称")
    private String name;

    @ApiModelProperty("秒杀图片")
    private String image;

    @ApiModelProperty("销售量")
    private Integer salesVolume;

    @ApiModelProperty("原售价")
    private BigDecimal maxPrice;

    @ApiModelProperty("最低价")
    private BigDecimal minSeckillPrice;

    @ApiModelProperty("最高价")
    private BigDecimal maxSeckillPrice;

    @ApiModelProperty("活动状态: [1=待发布, 2=未开始，3=进行中, 4=已结束]")
    private Integer status;

}
