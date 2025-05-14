package com.mdd.front.vo.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "我的优惠券Vo")
public class CouponMyListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    private Integer id;

    @ApiModelProperty(value = "领取记录ID")
    private Integer couponListId;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal money;

    @ApiModelProperty(value = "使用场景")
    private String useScene;

    @ApiModelProperty(value = "使用条件")
    private String condition;

    @ApiModelProperty(value = "订单满多少可用")
    private BigDecimal conditionMoney;

    @ApiModelProperty(value = "使用条件类型: [1=无门槛, 2=订单满足金额]")
    private Integer  conditionType;

    @ApiModelProperty(value = "适用商品类型: [1=全部商品,2=指定商品,3=指定商品不可用]")
    private Integer useGoodsType;

    @ApiModelProperty(value = "用券时间类型: [1=固定时间, 2=领券当天起, 3=领券次日起]")
    private Integer useTimeType;

    @ApiModelProperty(value = "多少天内可用")
    private Integer useTime;

    @ApiModelProperty(value = "用券开始时间")
    private Long useTimeStart;

    @ApiModelProperty(value = "用券结束时间")
    private Long useTimeEnd;

    @ApiModelProperty(value = "失效时间")
    private Long invalidTime;

    @ApiModelProperty(value = "领券时间")
    private Long createTime;

    @ApiModelProperty(value = "有效时间")
    private String effectiveTime;

    @ApiModelProperty(value = "使用状态: [0=未使用，1=已使用，2=已过期，3=已作废]")
    private Integer status;

}
