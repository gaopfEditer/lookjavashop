package com.mdd.common.entity.distribution;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销会员等级升级条件实体")
public class DistributionLevelUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("等级ID")
    private Integer levelId;

    @ApiModelProperty("场景")
    private Integer scene;

    @ApiModelProperty("条件值")
    private BigDecimal value;

    @ApiModelProperty("删除状态: [1=是 0=否]")
    private Integer isDelete;

    @ApiModelProperty("修改时间")
    private Long createTime;

    @ApiModelProperty("创建时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
