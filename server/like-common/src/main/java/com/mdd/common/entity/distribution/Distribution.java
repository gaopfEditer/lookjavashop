package com.mdd.common.entity.distribution;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销会员实体")
public class Distribution implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("等级ID")
    private Integer levelId;

    @ApiModelProperty("是否分销会员: [0-否 1-是]")
    private Integer isDistribution;

    @ApiModelProperty("是否冻结: 0-否 1-是")
    private Integer isFreeze;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("成为分销会员时间")
    private Long distributionTime;

    @ApiModelProperty("删除状态: [1=是 0=否]")
    private Integer IsDelete;

    @ApiModelProperty("注销状态: [1=是 0=否]")
    private Integer IsClose;

    @ApiModelProperty("修改时间")
    private Long createTime;

    @ApiModelProperty("创建时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
