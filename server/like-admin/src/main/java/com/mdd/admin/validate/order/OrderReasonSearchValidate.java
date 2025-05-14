package com.mdd.admin.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("售后原因搜索参数")
public class OrderReasonSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("退款类型: [0=全部, 1=仅退款, 2=退货退款]")
    private Integer type;

    @ApiModelProperty("搜索关键词")
    private String keyword;

    @ApiModelProperty("是否显示: [0=否, 1=是]")
    private Integer isShow;

}
