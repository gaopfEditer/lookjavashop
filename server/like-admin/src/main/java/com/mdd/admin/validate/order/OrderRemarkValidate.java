package com.mdd.admin.validate.order;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@ApiModel("订单备注参数")
public class OrderRemarkValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "订单ID", required = true)
    private Integer id;

    @Length(max = 255, message = "备注信息不能超出255个字符")
    @ApiModelProperty(value = "备注信息")
    private String remarks;

}
