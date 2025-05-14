package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销申请详情Vo")
public class DistributionApplyDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    private Integer Id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "省份ID")
    private Integer province;

    @ApiModelProperty(value = "城市ID")
    private Integer city;

    @ApiModelProperty(value = "地区ID")
    private Integer district;

    @ApiModelProperty(value = "省份")
    private String provinceMsg;

    @ApiModelProperty(value = "城市")
    private String cityMsg;

    @ApiModelProperty(value = "地区")
    private String districtMsg;

    @ApiModelProperty(value = "申请原因")
    private String reason;

    @ApiModelProperty(value = "审核原因")
    private String auditRemark;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "状态描述")
    private String statusMsg;

    @ApiModelProperty(value = "申请时间")
    private String createTime;

}
