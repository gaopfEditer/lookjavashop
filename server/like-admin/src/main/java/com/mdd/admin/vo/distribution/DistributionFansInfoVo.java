package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销商下级详情Vo")
public class DistributionFansInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户编号")
    private Integer sn;

    @ApiModelProperty("用户昵称")
    private String nickname;

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
}
