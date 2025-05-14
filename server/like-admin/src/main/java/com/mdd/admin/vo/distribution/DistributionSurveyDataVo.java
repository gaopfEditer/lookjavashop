package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销概括信息Vo")
public class DistributionSurveyDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("今日入账佣金")
    private BigDecimal todayEarnings;

    @ApiModelProperty("今日新增待结算佣金")
    private BigDecimal todayWaitEarnings;

    @ApiModelProperty("待结算佣金")
    private BigDecimal totalWaitEarnings;

    @ApiModelProperty("累计已入账佣金")
    private BigDecimal totalEarnings;

    @ApiModelProperty("分销商数量")
    private Long distributionStore;

    @ApiModelProperty("分销商占比")
    private String distributionRatio;

}
