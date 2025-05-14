package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "申请售后Vo")
public class OrderAfterCreateVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "售后ID")
    private Integer id;

}
