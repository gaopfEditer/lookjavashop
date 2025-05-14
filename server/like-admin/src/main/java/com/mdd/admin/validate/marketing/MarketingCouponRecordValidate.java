package com.mdd.admin.validate.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("优惠券记录搜索参数")
public class MarketingCouponRecordValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("筛选类型: 0=未使用,1=已使用,2=已过期,3=已作废")
    private Integer type;

    @ApiModelProperty("搜索关键词")
    private String keyword;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("获取途径: 1=用户领取,2=商家发放")
    private Integer channel;

    @ApiModelProperty("发放人: 空=全部,0=系统,其它=管理员")
    private Integer issuer;

    @ApiModelProperty("领取开始时间")
    private Long startTime;

    @ApiModelProperty("领取结束时间")
    private Long endTime;

}
