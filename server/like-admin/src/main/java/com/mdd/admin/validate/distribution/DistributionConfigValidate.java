package com.mdd.admin.validate.distribution;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("分销配置参数")
public class DistributionConfigValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "请选择是否开启分销功能")
    @IntegerContains(values = {0, 1}, message = "分销功能开关状态异常")
    @ApiModelProperty("分销功能: 0=关闭, 1=开启")
    private Integer open;

    @NotNull(message = "请选择分销层级")
    @IntegerContains(values = {1, 2}, message = "分销层级状态异常")
    @ApiModelProperty("分销层级: 1=一级分销, 2=二级分销")
    private Integer level;

    @NotNull(message = "请选择是否开启自购返佣")
    @IntegerContains(values = {0, 1}, message = "自购返佣开关状态异常")
    @ApiModelProperty("自购返佣: 0=关闭, 1=开启")
    private Integer isSelfRebate;

    @NotNull(message = "请选择是否开启商品详情显示佣金")
    @IntegerContains(values = {0, 1}, message = "商品详情显示佣金开关状态异常")
    @ApiModelProperty("商品详情显示佣金: 0=隐藏, 1=显示")
    private Integer isEarningsShow;

    @NotNull(message = "请选择是否开启详情页佣金可见用")
    @IntegerContains(values = {1, 2}, message = "详情页佣金可见用开关状态异常")
    @ApiModelProperty("详情页佣金可见用：1=全部用户, 2=分销商户")
    private Integer isEarningsScope;

    @NotNull(message = "请选择开通分销商的条件")
    @IntegerContains(values = {1, 2, 3}, message = "开通分销商的条件态异常")
    @ApiModelProperty("开通分销商的条件: 1=无条件, 2=申请分销, 3=指定分销")
    private Integer openCondition;

    @NotNull(message = "请选择是否显示申请协议")
    @IntegerContains(values = {0, 1}, message = "是否显示申请协议件态异常")
    @ApiModelProperty("是否显示申请协议: 0=隐藏, 1=显示")
    private Integer protocolShow;

    @ApiModelProperty("申请协议显示内容")
    private String protocolContent;

    @ApiModelProperty("申请页顶部宣传图")
    private String applyImage;

    @ApiModelProperty("结算时机类型: 1=订单完成后")
    private Integer settlementTiming;

    @NotNull(message = "请选择是否显示申请协议")
    @Min(value = 0, message = "结算时机时长不能少于0")
    @Max(value = 999999, message = "结算时机时长不能大于0")
    @ApiModelProperty("结算时机时长: (天数)")
    private Float settlementTime;

}
