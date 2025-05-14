package com.mdd.admin.validate.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("优惠券搜索参数")
public class MarketingCouponSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "筛选类型: 0=全部, 1=待发布,2=未开始, 3=进行中, 4=已结束")
    private Integer type;

    @ApiModelProperty(value = "领取限制类型: 1=用户领取, 2=商家赠送")
    private Integer getType;

    @ApiModelProperty(value = "适用商品类型: 1=全部商品,2=指定商品,3=指定商品不可用")
    private Integer useGoodsType;

    @ApiModelProperty("关键词")
    private String keyword;

}
