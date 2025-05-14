package com.mdd.admin.vo.decorate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("装修商品数据Vo")
public class DecorateDataGoodsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品图片")
    private String image;

    @ApiModelProperty("销售量")
    private Integer salesNum;

    @ApiModelProperty("最低价")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价")
    private BigDecimal maxPrice;

}
