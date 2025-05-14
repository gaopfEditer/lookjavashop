package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销商品列表Vo")
public class DistributionGoodsListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("最低价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal maxPrice;

    @ApiModelProperty("规格类型：[1=单规格, 2=多规格]")
    private Integer specType;

    @ApiModelProperty("商品状态: [0=下架, 1=上架]")
    private String goodsStatus;

    @ApiModelProperty("分销状态: [0=不参与, 1=参与]")
    private String distributionStatus;

}
