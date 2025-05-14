package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("分销等级Vo")
public class DistributionLevelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "等级id")
    private Integer levelId;

    @ApiModelProperty(value = "等级名称")
    private String levelName;

    @ApiModelProperty(value = "自购佣金比例")
    private Double selfRatio;

    @ApiModelProperty(value = "一级佣金比例")
    private Double firstRatio;

    @ApiModelProperty(value = "二级佣金比例")
    private Double secondRatio;

    @ApiModelProperty(value = "是否为当前等级")
    private Integer isNowLevel;

    @ApiModelProperty(value = "等级图标")
    private String icon;

    @ApiModelProperty(value = "等级背景图")
    private String image;

    @ApiModelProperty(value = "条件描述")
    private String conditionMsg;

    @ApiModelProperty(value = "条件")
    private List<condition> condition;

    @Data
    public static class condition {
        @ApiModelProperty(value = "等级id")
        private Integer levelId;

        @ApiModelProperty(value = "是否已完成")
        private Integer isFinish;

        @ApiModelProperty(value = "进度描述")
        private String progressMsg;

        @ApiModelProperty(value = "条件描述")
        private String conditionMsg;
    }

}



