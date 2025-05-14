package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销下级列表Vo")
public class DistributionFansListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户编号")
    private Integer sn;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("上级分销商")
    private Integer firstLeader;

    @ApiModelProperty("上级分销商")
    private String firstLeaderName;

    @ApiModelProperty("可提现佣金")
    private BigDecimal earnings;

    @ApiModelProperty("获得总佣金")
    private BigDecimal totalEarnings;

    @ApiModelProperty("状态")
    private Integer isFreeze;

    @ApiModelProperty("状态")
    private String isFreezeMsg;

    @ApiModelProperty("分销资格")
    private Integer isDistribution;

    @ApiModelProperty("分销资格")
    private String isDistributionMsg;

    @ApiModelProperty("成为分销商时间")
    private String distributionTime;

    @ApiModelProperty("注册时间")
    private String createTime;

}
