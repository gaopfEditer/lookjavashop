package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销订单列表Vo")
public class DistributionOrderListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("记录ID")
    private Integer id;

    @ApiModelProperty("订单商品id")
    private Integer orderGoodsId;

    @ApiModelProperty("买家ID")
    private Integer buyerId;

    @ApiModelProperty("买家编号")
    private Integer buyerSn;

    @ApiModelProperty("买家账号")
    private String buyerUsername;

    @ApiModelProperty("买家昵称")
    private String buyerNickname;

    @ApiModelProperty("买家头像")
    private String buyerAvatar;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("商品规格ID")
    private Integer goodsSkuId;

    @ApiModelProperty("商品图")
    private String goodsImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品规格描述")
    private String goodsSkuStr;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品数量")
    private Integer goodNum;

    @ApiModelProperty("实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty("分销等级")
    private String level;

    @ApiModelProperty("分销商id")
    private Integer distributionStoreId;

    @ApiModelProperty("分销商")
    private String distributionStore;

    @ApiModelProperty("佣金比例")
    private Double ratio;

    @ApiModelProperty("佣金")
    private BigDecimal earnings;

    @ApiModelProperty("佣金状态: [1-待结算 2-已结算 3-已失效]")
    private Integer status;

    @ApiModelProperty("佣金状态")
    private String statusMsg;

    @ApiModelProperty("结算时间")
    private String settleTime;

    @ApiModelProperty("下单时间")
    private String orderCreateTime;

}
