package com.mdd.common.entity.distribution;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销申请实体")
public class DistributionApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("省份")
    private Integer province;

    @ApiModelProperty("城市")
    private Integer city;

    @ApiModelProperty("县区")
    private Integer district;

    @ApiModelProperty("申请原因")
    private String reason;

    @ApiModelProperty("审核原因")
    private String auditRemark;

    @ApiModelProperty("审核时间")
    private Long auditTime;

    @ApiModelProperty("状态: [0-待审核；1-审核通过；2-审核不通过]")
    private Integer status;

    @ApiModelProperty("删除状态: [1=是 0=否]")
    private Integer IsDelete;

    @ApiModelProperty("修改时间")
    private Long createTime;

    @ApiModelProperty("创建时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
