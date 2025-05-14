package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销商搜索参数")
public class DistributionStoreSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户信息")
    private String keyword;

    @ApiModelProperty("分销等级")
    private Integer levelId;

    @ApiModelProperty("分销状态: 0=正常, 1=冻结")
    private Integer isFreeze;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

}
