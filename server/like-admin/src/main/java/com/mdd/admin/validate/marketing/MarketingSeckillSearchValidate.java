package com.mdd.admin.validate.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("秒杀搜索参数")
public class MarketingSeckillSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "筛选类型: [1=待发布, 2=未开始，3=进行中, 4=已结束]")
    private Integer type;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

}
