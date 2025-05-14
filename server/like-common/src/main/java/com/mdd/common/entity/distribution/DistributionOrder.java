package com.mdd.common.entity.distribution;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销订单实体")
public class DistributionOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("记录流水号")
    private String sn;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单商品ID")
    private Integer orderGoodsId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("商品规格ID")
    private Integer skuId;

    @ApiModelProperty("分销会员等级")
    private Integer levelId;

    @ApiModelProperty("分销层级")
    private Integer level;

    @ApiModelProperty("佣金")
    private BigDecimal earnings;

    @ApiModelProperty("分销比例")
    private Double ratio;

    @ApiModelProperty("删除状态: [1=是 0=否]")
    private Integer IsDelete;

    @ApiModelProperty("状态: [1-未返佣金 2-已返佣金 3-佣金失效]")
    private Integer status;

    @ApiModelProperty("结算时间")
    private Long settleTime;

    @ApiModelProperty("修改时间")
    private Long createTime;

    @ApiModelProperty("创建时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;
}
