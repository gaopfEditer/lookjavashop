package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销配置Vo")
public class DistributionConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分销功能: 0=关闭, 1=开启")
    private Integer open;

    @ApiModelProperty(value = "分销层级: 1=一级分销, 2=二级分销")
    private Integer level;

    @ApiModelProperty(value = "分销层级: 自购返佣: 0=关闭, 1=开启")
    private Integer isSelfRebate;

    @ApiModelProperty(value = "商品详情显示佣金: 0=隐藏, 1=显示")
    private Integer isEarningsShow;

    @ApiModelProperty(value = "详情页佣金可见用：1=全部用户, 2=分销商户")
    private Integer isEarningsScope;

    @ApiModelProperty(value = "开通分销商的条件: 1=无条件, 2=申请分销, 3=指定分销")
    private Integer openCondition;

    @ApiModelProperty(value = "是否显示申请协议: 0=隐藏, 1=显示")
    private Integer protocolShow;

    @ApiModelProperty(value = "申请协议显示内容")
    private String protocolContent;

    @ApiModelProperty(value = "申请页顶部宣传图")
    private String applyImage;

    @ApiModelProperty(value = "佣金计算方式: 1=商品实际支付金额")
    private Integer earningsCalMethod;

    @ApiModelProperty(value = "结算时机类型: 1=订单完成后")
    private Integer settlementTiming;

    @ApiModelProperty(value = "结算时机时长: (天数)")
    private String settlementTime;

}
