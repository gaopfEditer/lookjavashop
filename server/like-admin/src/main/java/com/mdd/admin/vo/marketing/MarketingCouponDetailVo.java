package com.mdd.admin.vo.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("营销优惠券详情Vo")
public class MarketingCouponDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券编号")
    private String sn;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券面额")
    private BigDecimal money;

    @ApiModelProperty(value = "发放数量限制: 1=无限数量, 2=固定发放数量")
    private Integer sendTotalType;

    @ApiModelProperty(value = "发放数量")
    private Integer sendTotal;

    @ApiModelProperty(value = "使用条件类型: 1=无门槛, 2=订单满足金额")
    private Integer conditionType;

    @ApiModelProperty(value = "使用条件(订单满多少可用)")
    private BigDecimal conditionMoney;

    @ApiModelProperty(value = "用券时间类型: [1=固定时间, 2=领券当天起, 3=领券次日起]")
    private Integer useTimeType;

    @ApiModelProperty(value = "用券开始时间: use_time_type=1时生效")
    private String useTimeStart;

    @ApiModelProperty(value = "用券结束时间: use_time_type=1时生效")
    private String useTimeEnd;

    @ApiModelProperty(value = "多少天内可用: use_time_type=2/3时生效")
    private Integer useTime;

    @ApiModelProperty(value = "领取限制类型: 1=用户领取, 2=商家赠送")
    private Integer getType;

    @ApiModelProperty(value = "领取次数类型: 1=不限制领取次数, 2=限制次数, 3=每天限制数量")
    private Integer getNumType;

    @ApiModelProperty(value = "领取次数类型: get_type=2/3时生效")
    private Integer getNum;

    @ApiModelProperty(value = "领取开始时间")
    private String getTimeStart;

    @ApiModelProperty(value = "领取结束时间")
    private String getTimeEnd;

    @ApiModelProperty(value = "适用商品类型: 1=全部商品,2=指定商品,3=指定商品不可用")
    private Integer useGoodsType;

    @ApiModelProperty(value = "使用商品ID")
    private String useGoodsIds;

    @ApiModelProperty(value = "使用商品列表")
    private List<MarketingCouponDetailVo.CouponGoodsItemVo> useGoodsList;

    @Data
    public static class CouponGoodsItemVo {
        private Integer id;
        private String image;
        private String code;
        private String name;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
    }

}
