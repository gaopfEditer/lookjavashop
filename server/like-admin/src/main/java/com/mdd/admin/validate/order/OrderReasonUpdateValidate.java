package com.mdd.admin.validate.order;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("售后原因更新参数")
public class OrderReasonUpdateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必须为大于0的数字")
    @ApiModelProperty("ID")
    private Integer id;

    @NotNull(message = "type参数缺失")
    @ApiModelProperty("退款类型: [0=全部, 1=仅退款, 2=退货退款]")
    private Integer type;

    @NotNull(message = "content参数缺失")
    @ApiModelProperty("售后原因")
    private String content;

    @Min(value = 0, message = "排序号不能少于0")
    @Max(value = 9999, message = "排序号不能大于9999")
    @ApiModelProperty("排序编号")
    private Integer sort = 0;

    @NotNull(message = "isShow参数缺失")
    @ApiModelProperty("是否显示: [0=否, 1=是]")
    private Integer isShow;

}
