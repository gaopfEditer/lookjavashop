package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销订单Vo")
public class DistributionOrderVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    private Integer id;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty(value = "佣金")
    private BigDecimal earnings;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "状态: [状态：1-待结算 2-已结算 3-已失效]")
    private String statusMsg;

    @ApiModelProperty(value = "订单时间")
    private String createTime;


}
