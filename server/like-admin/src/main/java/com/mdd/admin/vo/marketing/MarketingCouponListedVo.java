package com.mdd.admin.vo.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("营销优惠券列表Vo")
public class MarketingCouponListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券编号")
    private String sn;

    @ApiModelProperty(value = "优惠券内容")
    private String discountContent;

    @ApiModelProperty(value = "发放数量描述")
    private String sendTotalMsg;

    @ApiModelProperty(value = "使用时间描述")
    private String useTimeMsg;

    @ApiModelProperty(value = "领取方式描述")
    private String getTypeMsg;

    @ApiModelProperty(value = "优惠券状态描述")
    private String statusMsg;

    @ApiModelProperty(value = "领取次数类型描述")
    private String getNumTypeMsg;

    @ApiModelProperty(value = "适用商品类型描述")
    private String useGoodsTypeMsg;

    @ApiModelProperty(value = "发放状态: [1=待发布,2=未开始, 3=进行中, 4=已结束]")
    private Integer status;

    @ApiModelProperty(value = "领取次数类型: 1=不限制领取次数, 2=限制次数, 3=每天限制数量")
    private Integer getNumType;

    @ApiModelProperty(value = "适用商品类型: 1=全部商品,2=指定商品,3=指定商品不可用")
    private Integer useGoodsType;

    @ApiModelProperty(value = "使用数量")
    private Long useNumber;

    @ApiModelProperty(value = "领取数量")
    private Long receiveNumber;

    @ApiModelProperty(value = "剩余数量")
    private String surplusNumber;

    @ApiModelProperty(value = "使用率(%)")
    private String usageRate;

    @ApiModelProperty("领取时间")
    private String getTimeMsg;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
