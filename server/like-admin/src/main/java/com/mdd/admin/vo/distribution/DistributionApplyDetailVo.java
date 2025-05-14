package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销申请详情Vo")
public class DistributionApplyDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("记录ID")
    private Integer id;

    @ApiModelProperty("用户编号")
    private String sn;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邀请人")
    private String inviter;

    @ApiModelProperty("省份id")
    private Integer province;

    @ApiModelProperty("城市id")
    private Integer city;

    @ApiModelProperty("市区id")
    private Integer district;

    @ApiModelProperty("地区")
    private String region;

    @ApiModelProperty("审核状态: [1-待审核; 2-审核通过; 3-审核不通过]")
    private Integer status;

    @ApiModelProperty("审核状态")
    private String statusMsg;

    @ApiModelProperty("申请原因")
    private String reason;

    @ApiModelProperty("审核说明")
    private String auditRemark;

    @ApiModelProperty("审核时间")
    private String auditTime;

    @ApiModelProperty("申请时间")
    private String createTime;

}
