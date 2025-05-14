package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销等级详情Vo")
public class DistributionLevelDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("等级权重")
    private Integer weights;

    @ApiModelProperty("等级描述")
    private String remark;

    @ApiModelProperty("一级佣金比例")
    private Double firstRatio;

    @ApiModelProperty("二级佣金比例")
    private Double secondRatio;

    @ApiModelProperty("自购佣金比例")
    private Double selfRatio;

    @ApiModelProperty("等级图标")
    private String icon;

    @ApiModelProperty("等级背景图")
    private String image;

    @ApiModelProperty("是否默认等级: [0-否 1-是]")
    private Integer isDefault;

    @ApiModelProperty("等级更新条件类型: 1=满足以下任何条件 2=满足以下全部条件")
    private Integer updateType;

    @ApiModelProperty("单笔消费金额")
    private BigDecimal singleConsumptionAmount;

    @ApiModelProperty("累计消费金额")
    private BigDecimal cumulativeConsumptionAmount;

    @ApiModelProperty("累计消费次数")
    private Integer cumulativeConsumptionTimes;

    @ApiModelProperty("已结算佣金收入")
    private BigDecimal returnedCommission;
}
