package com.mdd.admin.validate.marketing;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("优惠券更新参数")
public class MarketingCouponUpdateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "ID")
    private Integer id;

    @NotNull(message = "name参数缺失")
    @Length(min = 1, max = 200, message = "优惠券名称最少1个字符,最大200个字符")
    @ApiModelProperty(value = "优惠券名称", required = true)
    private String name;

    @NotNull(message = "money参数缺失")
    @DecimalMin(value = "0.01", message = "优惠券面额不能少于0.01")
    @ApiModelProperty(value = "优惠券面额", required = true)
    private BigDecimal money;

    @IntegerContains(values = {1, 2}, message = "发放数量限制不在可选值内[1,2]")
    @ApiModelProperty(value = "发放数量限制: 1=无限数量, 2=固定发放数量", required = true)
    private Integer sendTotalType;

    @DecimalMin(value = "0", message = "发放数量不能少于0")
    @ApiModelProperty(value = "发放数量")
    private Integer sendTotal;

    @IntegerContains(values = {1, 2}, message = "使用条件类型不在可选值内[1,2]")
    @ApiModelProperty(value = "使用条件类型: 1=无门槛, 2=订单满足金额", required = true)
    private Integer conditionType;

    @DecimalMin(value = "0", message = "订单满多少可用不能少于0")
    @ApiModelProperty(value = "使用条件(订单满多少可用)")
    private BigDecimal conditionMoney;

    @IntegerContains(values = {1, 2, 3}, message = "使用条件类型不在可选值内[1,2,3]")
    @ApiModelProperty(value = "用券时间类型: [1=固定时间, 2=领券当天起, 3=领券次日起]", required = true)
    private Integer useTimeType;

    @ApiModelProperty(value = "用券开始时间: use_time_type=1时生效")
    private String useTimeStart;

    @ApiModelProperty(value = "用券结束时间: use_time_type=1时生效")
    private String useTimeEnd;

    @DecimalMin(value = "0", message = "多少天内可用值不能少于1")
    @ApiModelProperty(value = "多少天内可用: use_time_type=2/3时生效")
    private Integer useTime;

    @IntegerContains(values = {1, 2}, message = "领取限制类型[1,2]")
    @ApiModelProperty(value = "领取限制类型: 1=用户领取, 2=商家赠送")
    private Integer getType;

    @IntegerContains(values = {1, 2, 3}, message = "领取次数类型[1,2, 3]")
    @ApiModelProperty(value = "领取次数类型: 1=不限制领取次数, 2=限制次数, 3=每天限制数量", required = true)
    private Integer getNumType;

    @DecimalMin(value = "0", message = "领取次数限制不能少于0")
    @ApiModelProperty(value = "领取次数类型: get_type=2/3时生效")
    private Integer getNum;

    @NotNull(message = "getTimeStart参数缺失")
    @ApiModelProperty(value = "领取开始时间")
    private String getTimeStart;

    @NotNull(message = "getTimeEnd参数缺失")
    @ApiModelProperty(value = "领取结束时间")
    private String getTimeEnd;

    @NotNull(message = "useGoodsType参数缺失")
    @IntegerContains(values = {1, 2, 3}, message = "适用商品类型不在合法值内[1, 2, 3]")
    @ApiModelProperty(value = "适用商品类型: 1=全部商品,2=指定商品,3=指定商品不可用")
    private Integer useGoodsType;

    @ApiModelProperty(value = "使用商品ID")
    private String useGoodsIds;

}
