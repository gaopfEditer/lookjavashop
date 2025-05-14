package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DistributionGoodsDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("商品编码")
    private String code;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("是否参与分销: [0=不参与 1=参与]")
    private Integer isDistribution;

    @ApiModelProperty("佣金规则: [1-按分销等级比例分佣 2-单独设置分佣比例]")
    private Integer rule;

    @ApiModelProperty("佣金信息")
    private List<ratioData> ratioData;

    @Data
    public static class ratioData {
        @ApiModelProperty("分销等级ID")
        private Integer id;

        @ApiModelProperty("是否默认等级")
        private Integer isDefault;

        @ApiModelProperty("等级名称")
        private String name;

        @ApiModelProperty("自购佣金")
        private Double selfRatio;

        @ApiModelProperty("一级佣金")
        private Double firstRatio;

        @ApiModelProperty("二级佣金")
        private Double secondRatio;

        @ApiModelProperty("规格")
        private List<DistributionGoodsDetailVo.skuItem> skuItems;
    }

    @Data
    public static class skuItem {
        @ApiModelProperty("规格ID")
        private Integer id;

        @ApiModelProperty("等级ID")
        private Integer levelId;

        @ApiModelProperty("自购佣金")
        private Double selfRatio;

        @ApiModelProperty("一级佣金")
        private Double firstRatio;

        @ApiModelProperty("二级佣金")
        private Double secondRatio;

        @ApiModelProperty("售价")
        private BigDecimal sellPrice;

        @ApiModelProperty("规格")
        private String specValueStr;
    }
}
