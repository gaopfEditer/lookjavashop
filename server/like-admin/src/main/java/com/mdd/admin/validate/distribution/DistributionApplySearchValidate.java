package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销申请搜索参数")
public class DistributionApplySearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户搜索关键词")
    private String searchUserKeyword;

    @ApiModelProperty(value = "邀请人搜索关键词")
    private String searchInviterKeyword;

    @ApiModelProperty("状态: [0= 全部; 1-待审核; 2-审核通过; 3-审核不通过]")
    private Integer status;

    @ApiModelProperty(value = "申请开始时间")
    private String startTime;

    @ApiModelProperty(value = "申请结束时间")
    private String endTime;

}
