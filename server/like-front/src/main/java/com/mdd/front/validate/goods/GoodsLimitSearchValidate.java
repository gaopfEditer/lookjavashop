package com.mdd.front.validate.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品搜素参数")
public class GoodsLimitSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型: [new=最新, hot=热门, like=猜你喜欢]")
    private String type;

    @ApiModelProperty(value = "限制数量")
    private Integer limit;

    @ApiModelProperty(value = "商品分类ID(逗号分隔)")
    private String categoryId;

}
