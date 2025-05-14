package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销等级列表Vo")
public class DistributionLevelListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("等级权重")
    private Integer weights;

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

    @ApiModelProperty("分销商人数")
    private Long memberNum;

}
