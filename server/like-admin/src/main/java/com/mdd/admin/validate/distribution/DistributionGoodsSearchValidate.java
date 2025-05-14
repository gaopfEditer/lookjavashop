package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("分销商品搜索参数")
public class DistributionGoodsSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品信息")
    private String keyword;

    @ApiModelProperty("商品分类")
    private Integer categoryId;

    @ApiModelProperty("商品状态: 0=否, 1=是, 2=已售罄")
    private Integer status;

    @ApiModelProperty("是否参与分销: 0=否, 1=是")
    private Integer isDistribution;

}
