package com.mdd.admin.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("售后原因创建参数")
public class OrderReasonCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "type参数缺失")
    @ApiModelProperty("退款类型: [0=全部, 1=仅退款, 2=退货退款]")
    private Integer type;

    @NotNull(message = "content参数缺失")
    @ApiModelProperty("售后原因")
    private String content;

    @Min(value = 0, message = "排序号不能少于0")
    @Max(value = 0, message = "排序号不能大于9999")
    @ApiModelProperty("排序编号")
    private Integer sort = 0;

    @NotNull(message = "isShow参数缺失")
    @ApiModelProperty("是否显示: [0=否, 1=是]")
    private Integer isShow;

}
