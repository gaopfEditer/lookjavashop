package com.mdd.admin.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("规格值列表")
public class SpecValueListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("IDS")
    private String ids;

    @ApiModelProperty("值")
    private String value;
}
