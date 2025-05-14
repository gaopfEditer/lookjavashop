package com.mdd.common.entity.distribution;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销商品实体")
public class DistributionGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("商品id")
    private Integer goodsId;

    @ApiModelProperty("商品规格id")
    private Integer skuId;

    @ApiModelProperty("分销会员等级id")
    private Integer levelId;

    @ApiModelProperty("自购佣金比例")
    private Double selfRatio;

    @ApiModelProperty("一级佣金比例")
    private Double firstRatio;

    @ApiModelProperty("二级佣金比例")
    private Double secondRatio;

    @ApiModelProperty("是否参与分销: [0-不参与 1-参与]")
    private Integer isDistribution;

    @ApiModelProperty("佣金规则: [1-按分销等级比例分佣 2-单独设置分佣比例]")
    private Integer rule;

    @ApiModelProperty("删除状态: [1=是 0=否]")
    private Integer IsDelete;

    @ApiModelProperty("修改时间")
    private Long createTime;

    @ApiModelProperty("创建时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
