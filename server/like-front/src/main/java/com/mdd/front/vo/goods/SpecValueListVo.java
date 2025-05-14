package com.mdd.front.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "sku值集合Vo")
public class SpecValueListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value =  "ids")
    private String ids;

    @ApiModelProperty(value =  "sku值")
    private String value;
}
