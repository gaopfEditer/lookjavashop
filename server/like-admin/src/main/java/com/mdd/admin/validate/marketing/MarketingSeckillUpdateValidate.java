package com.mdd.admin.validate.marketing;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("秒杀更新参数")
public class MarketingSeckillUpdateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "ID")
    private Integer id;

    @NotNull(message = "name参数缺失")
    @Length(max = 250, message = "活动名称不能超出250个字符")
    @ApiModelProperty(value = "活动名称")
    private String name;

    @Length(max = 250, message = "活动备注不能超出250个字符")
    @ApiModelProperty(value = "活动备注")
    private String remarks;

    @NotNull(message = "startTime参数缺失")
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @NotNull(message = "endTime参数缺失")
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @NotNull(message = "limitStatus参数缺失")
    @IntegerContains(values = {0, 1})
    @ApiModelProperty(value = "每单限制: [0=不限制, 1=限制]")
    private Integer limitStatus;

    @ApiModelProperty(value = "单笔最多购买的商品件数")
    private Integer maxBuy = 0;

    @NotNull(message = "goods参数缺失")
    @ApiModelProperty(value = "活动商品")
    private List<MarketingSeckillUpdateValidate.GoodsItemValidate> goods;

    @Data
    public static class GoodsItemValidate {
        private Integer id;
        private Integer specType;
        private Integer salesVolume = 0;
        private Integer browseVolume = 0;
        private List<MarketingSeckillUpdateValidate.SkuPriceValidate> prices;
    }

    @Data
    public static class SkuPriceValidate {
        private Integer skuId;
        private BigDecimal seckillPrice;
    }

}
