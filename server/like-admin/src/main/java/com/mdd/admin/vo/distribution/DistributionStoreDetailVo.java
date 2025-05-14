package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销商详情Vo")
public class DistributionStoreDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户编号")
    private Integer sn;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("分销等级名ID")
    private Integer levelId;

    @ApiModelProperty("分销等级名称")
    private String levelName;

    @ApiModelProperty("上级")
    private Integer firstLeader;

    @ApiModelProperty("上级名称")
    private String firstLeaderName;

    @ApiModelProperty("已入账佣金")
    private BigDecimal receivedEarnings;

    @ApiModelProperty("待结算佣金")
    private BigDecimal waitEarnings;

    @ApiModelProperty("分销订单数量")
    private Long distributionOrderNum;

    @ApiModelProperty("下级人数")
    private Integer fans;

    @ApiModelProperty("下级分销商人数")
    private Long fansDistribution;

    @ApiModelProperty("下一级人数")
    private Integer fansFirst;

    @ApiModelProperty("下一级人数(分销商)")
    private Long fansFirstDistribution;

    @ApiModelProperty("下二级人数")
    private Integer fansSecond;

    @ApiModelProperty("下二级人数(分销商)")
    private Long fansSecondDistribution;

    @ApiModelProperty("状态: [0=正常 1=冻结]")
    private Integer isFreeze;

    @ApiModelProperty("注销状态: [0=正常 1=注销]")
    private Integer isClose;

    @ApiModelProperty("状态")
    private String isFreezeMsg;

    @ApiModelProperty("成为分销商时间")
    private String distributionTime;
}
