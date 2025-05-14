package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销页信息Vo")
public class DistributionIndexVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "邀请码")
    private String code;

    @ApiModelProperty(value = "上级分销商")
    private String firstLeader;

    @ApiModelProperty(value = "分销等级ID")
    private Integer levelId;

    @ApiModelProperty(value = "分销等级")
    private String levelName;

    @ApiModelProperty(value = "等级图标")
    private String levelIcon;

    @ApiModelProperty(value = "等级背景")
    private String levelImage;

    @ApiModelProperty(value = "可提现金额")
    private BigDecimal earnings;

    @ApiModelProperty(value = "今日预估收益")
    private BigDecimal todayEarnings;

    @ApiModelProperty(value = "本月预估收益")
    private BigDecimal monthEarnings;

    @ApiModelProperty(value = "累计获得收益")
    private BigDecimal totalEarnings;

    @ApiModelProperty(value = "粉丝数量")
    private Long totalFans;

    @ApiModelProperty(value = "分销订单数量")
    private Long totalOrder;

    @ApiModelProperty(value = "背景图")
    private String applyImage;

    @ApiModelProperty(value = "是否显示协议: [1=显示 0=不显示]")
    private Integer isShowProtocol;

    @ApiModelProperty(value = "是否分销会员: [1=是 0=不是]")
    private Integer isDistribution;

    @ApiModelProperty(value = "是否显示申请页面: [1=显示 0=不显示]")
    private Integer isShowApplyPage;
}
