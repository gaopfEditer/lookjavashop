package com.mdd.common.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("订单售后商品实体")
public class OrderAfterGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("售后订单ID")
    private Integer orderAfterId;

    @ApiModelProperty("订单商品ID")
    private Integer orderGoodsId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("订单规格ID")
    private Integer goodsSkuId;

    @ApiModelProperty("商品数量")
    private Integer goodsNum;

    @ApiModelProperty("商品单价")
    private BigDecimal goodsPrice;

    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;
}
