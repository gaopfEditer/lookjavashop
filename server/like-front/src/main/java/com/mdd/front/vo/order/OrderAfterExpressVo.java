package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "售后物流公司Vo")
public class OrderAfterExpressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物流公司ID")
    private Integer Id;

    @ApiModelProperty(value = "物流公司名称")
    private String name;
}
