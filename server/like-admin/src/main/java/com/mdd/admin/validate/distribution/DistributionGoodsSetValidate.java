package com.mdd.admin.validate.distribution;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("分销商品设置佣金参数")
public class DistributionGoodsSetValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "商品id参数必传且需大于0")
    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @NotNull(message = "分销状态参数缺失")
    @IntegerContains(values = {0, 1}, message = "分销状态异常")
    @ApiModelProperty("是否参与分销: [0=不参与 1=参与]")
    private Integer isDistribution;

    @NotNull(message = "佣金规则参数缺失")
    @IntegerContains(values = {1, 2}, message = "请选择佣金规则")
    @ApiModelProperty("佣金规则: [1-按分销等级比例分佣 2-单独设置分佣比例]")
    private Integer rule;

    @ApiModelProperty("佣金信息")
    private List<DistributionGoodsSetValidate.ratioData> ratioData;

    @Data
    public static class ratioData {
        @ApiModelProperty("等级ID")
        private Integer levelId;

        @ApiModelProperty("规格ID")
        private Integer skuId;

        @ApiModelProperty("自购佣金")
        private Double selfRatio;

        @ApiModelProperty("一级佣金")
        private Double firstRatio;

        @ApiModelProperty("二级佣金")
        private Double secondRatio;
    }


}
