package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销用户信息Vo")
public class DistributionOrderUserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "分销等级")
    private Integer levelId;

    @ApiModelProperty(value = "是否为分销会员")
    private Integer isDistribution;

    @ApiModelProperty(value = "是否冻结")
    private Integer isFreeze;

    @ApiModelProperty(value = "上级ID")
    private Integer firstLeader;

    @ApiModelProperty(value = "上上级ID")
    private Integer secondLeader;

}
