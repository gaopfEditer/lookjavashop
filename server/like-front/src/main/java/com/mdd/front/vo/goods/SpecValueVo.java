package com.mdd.front.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "sku值Vo")
public class SpecValueVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value =  "ID")
    private Integer id;

    @ApiModelProperty(value =  "sku名称")
    private String name;

    @ApiModelProperty(value =  "sku集合")
    private List<SpecListVo> specList;


}
