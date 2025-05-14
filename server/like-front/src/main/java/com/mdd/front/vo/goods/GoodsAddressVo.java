package com.mdd.front.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "商品默认地址Vo")
public class GoodsAddressVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "默认地址ID")
    private Integer addressId;

    @ApiModelProperty(value = "省份")
    private String provinceName;

    @ApiModelProperty(value = "城市")
    private String cityName;

    @ApiModelProperty(value = "地区")
    private String districtName;
}
