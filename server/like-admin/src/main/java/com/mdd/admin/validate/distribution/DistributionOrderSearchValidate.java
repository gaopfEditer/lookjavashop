package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销订单搜索参数")
public class DistributionOrderSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("商品关键词")
    private String goodsKeyword;

    @ApiModelProperty("买家关键词")
    private String buyerKeyword;

    @ApiModelProperty("分销商关键词")
    private String distributionKeyword;

    @ApiModelProperty("佣金状态: [1-待结算 2-已结算 3-已失效]")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Integer startTime;

    @ApiModelProperty(value = "结束时间")
    private Integer endTime;

}
