package com.mdd.admin.vo.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("营销秒杀详情Vo")
public class MarketingSeckillDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID")
    private Integer id;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "活动备注")
    private String remarks;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "每单限制: [0=不限制, 1=限制]")
    private Integer limitStatus;

    @ApiModelProperty(value = "单笔最多购买的商品件数")
    private Integer maxBuy = 0;

    @ApiModelProperty(value = "活动商品")
    private List<MarketingSeckillDetailVo.SeckillGoodsItemVo> goods;

    @Data
    public static class SeckillGoodsItemVo {
        private Integer id;
        private Integer seckillGid;
        private String image;
        private String name;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private Integer specType;
        private Integer salesVolume;
        private Integer browseVolume;
        private List<MarketingSeckillDetailVo.SeckillGoodsSkuVo> prices;
    }

    @Data
    public static class SeckillGoodsSkuVo {
        private Integer id;
        private String skuValueArr;
        private Integer stock;
        private BigDecimal price;
        private BigDecimal seckillPrice;
    }

}
