package com.mdd.front.validate.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品搜素参数")
public class GoodsListSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型: [hot=热门]")
    private String type;

    @ApiModelProperty(value = "关键词搜索")
    private String keyword;

    @ApiModelProperty(value = "排序: [priceAsc=价格升序 priceDesc=价格降序 salesAsc=销量升序 salesDesc=销量降序]")
    private String sort;

    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;

    @ApiModelProperty(value = "分类ID")
    private Integer categoryId;

}
