package com.mdd.admin.vo.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("营销优惠券记录Vo")
public class MarketingCouponRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("发放人ID")
    private Integer issuerId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("用户编号")
    private String userSn;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("领取方式: 1=用户领取,2=商家发放")
    private Integer channel;

    @ApiModelProperty("使用状态: 0=未使用,1=已使用,2=已过期,3=已作废")
    private Integer status;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("获取途径描述")
    private String channelMsg;

    @ApiModelProperty("发放人描述")
    private String issuerMsg;

    @ApiModelProperty("优惠券状态描述")
    private String statusMsg;

    @ApiModelProperty("使用时间")
    private String useTime;

    @ApiModelProperty("过期时间")
    private String invalidTime;

    @ApiModelProperty("领取时间")
    private String createTime;

}
