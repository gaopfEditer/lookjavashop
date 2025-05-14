package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销商下级搜索参数")
public class DistributionFansSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户Id")
    private Integer userId;

    @ApiModelProperty("用户信息")
    private String keyword;

    @ApiModelProperty("分销资格: [0=未开通 1=已开通]")
    private Integer isDistribution;

    @ApiModelProperty("下级层级: 1=下一级 2=下二级")
    private Integer level;

}
