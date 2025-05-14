package com.mdd.admin.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品列表Vo")
public class GoodsListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "货号")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "商品主图")
    private String image;

    @ApiModelProperty(value = "库存预警")
    private Integer stockWarning;

    @ApiModelProperty(value = "规格类型: 1=单规, 2=多规格")
    private Integer specType;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "售价")
    private String sellPrice;

    @ApiModelProperty(value = "总库存")
    private Integer totalStock;

    @ApiModelProperty(value = "销量")
    private Integer salesNum;

    @ApiModelProperty(value = "活动状态")
    private Integer isActivity;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
